package com.hiperium.city.tasks.api.service;

import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.exception.QuartzException;
import com.hiperium.city.tasks.api.exception.TaskException;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.utils.JobsUtil;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import com.hiperium.city.tasks.api.utils.enums.ResourceErrorEnum;
import com.hiperium.city.tasks.api.utils.enums.TaskErrorEnum;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    @Value("${hiperium.city.tasks.time.zone.id}")
    private String zoneId;

    private final Scheduler quartzScheduler;
    private final TaskRepository taskRepository;

    public TaskService(Scheduler quartzScheduler, TaskRepository taskRepository) {
        this.quartzScheduler = quartzScheduler;
        this.taskRepository = taskRepository;
    }

    public Mono<Task> create(Mono<Task> task) {
        return task
                .map(TasksUtil::validateTaskId)
                .map(this::createAndScheduleJob)
                .publishOn(Schedulers.boundedElastic())
                .map(scheduledTask -> {
                    scheduledTask.setCreatedAt(ZonedDateTime.now());
                    scheduledTask.setUpdatedAt(ZonedDateTime.now());
                    return this.taskRepository.save(scheduledTask);
                });
    }

    public Mono<Task> findById(Long id) {
        return Mono.fromSupplier(() -> this.taskRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(ResourceErrorEnum.TASK_NOT_FOUND, id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Task> findAll() {
        return Flux.fromIterable(this.taskRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Task> update(Long id, Mono<Task> task) {
        return this.findById(id)
                .map(this::getCurrentTrigger)
                .switchIfEmpty(task
                        .map(this::createAndScheduleJob)
                        .map(this::getCurrentTrigger))
                .publishOn(Schedulers.boundedElastic())
                // TODO: Find a way to avoid using "block()".
                .mapNotNull(currentTrigger -> this.rescheduleJob(currentTrigger, task.block()))
                .map(scheduledTask -> {
                    scheduledTask.setUpdatedAt(ZonedDateTime.now());
                    return this.taskRepository.save(scheduledTask);
                });
    }

    public Mono<Void> delete(final Long id) {
        return this.findById(id)
                .publishOn(Schedulers.boundedElastic())
                .map(task -> {
                    this.taskRepository.delete(task);
                    return task;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(this::getCurrentTrigger)
                .map(currentTrigger -> this.unscheduleJob(id, currentTrigger))
                .then();
    }

    private Task createAndScheduleJob(final Task task) {
        LOGGER.debug("createAndScheduleJob() - BEGIN: {}", task.getName());
        task.setJobId(TasksUtil.generateJobId());
        JobDetail job = JobsUtil.createJobDetailFromTask(task);
        Trigger trigger = JobsUtil.createCronTriggerFromTask(task, this.zoneId);
        try {
            this.quartzScheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new QuartzException(e);
        }
        LOGGER.debug("createAndScheduleJob() - END");
        return task;
    }

    private Trigger getCurrentTrigger(Task task) {
        LOGGER.debug("getCurrentTrigger() - BEGIN: {}", task.getId());
        if (Objects.isNull(task.getJobId())) {
            throw new TaskException(TaskErrorEnum.FIND_TRIGGER_WITHOUT_JOB_ID, task.getId());
        }
        Trigger trigger = null;
        try {
            for (JobKey jobKey : this.quartzScheduler.getJobKeys(GroupMatcher.jobGroupEquals(JobsUtil.TASK_GROUP_NAME))) {
                LOGGER.debug("Existing JobKey found: {}", jobKey);
                if (jobKey.getName().equals(task.getJobId())) {
                    TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobId(), JobsUtil.TASK_GROUP_NAME);
                    LOGGER.debug("Existing TriggerKey found: {}", triggerKey);
                    trigger = this.quartzScheduler.getTrigger(triggerKey);
                }
            }
        } catch (SchedulerException e) {
            throw new QuartzException(e);
        }
        LOGGER.debug("getTrigger() - END");
        return trigger;
    }

    private Task rescheduleJob(Trigger actualTrigger, Task task) {
        LOGGER.debug("Actual trigger to update: {}", actualTrigger);
        Trigger newTrigger = JobsUtil.createCronTriggerFromTask(task, this.zoneId);
        Date newTriggerFirstFire;
        try {
            newTriggerFirstFire = this.quartzScheduler.rescheduleJob(actualTrigger.getKey(), newTrigger);
            if (Objects.isNull(newTriggerFirstFire)) {
                throw new TaskException(TaskErrorEnum.CANNOT_RESCHEDULE_TRIGGER, task.getId());
            } else {
                LOGGER.info("Successfully rescheduled trigger for Task ID: {}.", task.getId());
            }
        } catch (SchedulerException e) {
            throw new QuartzException(e);
        }
        return task;
    }

    private Mono<Void> unscheduleJob(Long id, Trigger currentTrigger) {
        LOGGER.debug("createAndScheduleJob() - BEGIN: {}", id);
        try {
            boolean unscheduledJob = this.quartzScheduler.unscheduleJob(currentTrigger.getKey());
            if (unscheduledJob) {
                LOGGER.info("Job unscheduled for Task: {}", id);
            }
        } catch (SchedulerException e) {
            throw new QuartzException(e);
        }
        LOGGER.debug("createAndScheduleJob() - END");
        return Mono.empty();
    }
}
