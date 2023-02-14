package com.hiperium.city.tasks.api.service;

import com.hiperium.city.tasks.api.exception.QuartzException;
import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.utils.JobsUtil;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import com.hiperium.city.tasks.api.utils.enums.ResourceErrorEnum;
import com.hiperium.city.tasks.api.utils.enums.SchedulerErrorEnum;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.ZonedDateTime;
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

    public Mono<Task> create(final Task newTask) {
        return Mono.just(newTask)
                .doOnNext(this::scheduleJob)
                .doOnNext(scheduledTask -> scheduledTask.setCreatedAt(ZonedDateTime.now()))
                .doOnNext(scheduledTask -> scheduledTask.setUpdatedAt(ZonedDateTime.now()))
                .map(this.taskRepository::save);
    }

    public Mono<Task> findById(final Long id) {
        return Mono.fromSupplier(() -> this.taskRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(ResourceErrorEnum.TASK_NOT_FOUND, id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Task> findAll() {
        return Flux.fromIterable(this.taskRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Task> update(long taskId, final Task updatedTask) {
        return this.findById(taskId)
                .doOnNext(this::rescheduleJob)
                .doOnNext(scheduledTask -> BeanUtils.copyProperties(updatedTask, scheduledTask))
                .doOnNext(scheduledTask -> scheduledTask.setUpdatedAt(ZonedDateTime.now()))
                .map(this.taskRepository::save);
    }

    public Mono<Void> delete(final Long taskId) {
        return this.findById(taskId)
                .doOnNext(this::unscheduleJob)
                .doOnNext(this.taskRepository::delete)
                .then();
    }

    private void scheduleJob(final Task task) {
        LOGGER.debug("scheduleJob() - BEGIN: {}", task.getName());
        task.setJobId(TasksUtil.generateJobId());
        JobDetail job = JobsUtil.createJobDetailFromTask(task);
        Trigger trigger = JobsUtil.createCronTriggerFromTask(task, this.zoneId);
        try {
            this.quartzScheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new QuartzException(e, SchedulerErrorEnum.SCHEDULE_JOB_ERROR, task.getName());
        }
        LOGGER.debug("scheduleJob() - END");
    }

    private void rescheduleJob(final Task task) {
        LOGGER.debug("rescheduleJob() - BEGIN: {}", task.getId());
        Trigger currentTrigger = this.getCurrentTrigger(task);
        Trigger newTrigger = JobsUtil.createCronTriggerFromTask(task, this.zoneId);
        try {
            this.quartzScheduler.rescheduleJob(currentTrigger.getKey(), newTrigger);
        } catch (SchedulerException e) {
            throw new QuartzException(e, SchedulerErrorEnum.RESCHEDULE_JOB_ERROR, task.getId());
        }
        LOGGER.debug("rescheduleJob() - END");
    }

    private void unscheduleJob(final Task task) {
        LOGGER.debug("unscheduleJob() - BEGIN: {}", task.getId());
        Trigger currentTrigger = this.getCurrentTrigger(task);
        try {
            this.quartzScheduler.unscheduleJob(currentTrigger.getKey());
        } catch (SchedulerException e) {
            throw new QuartzException(e, SchedulerErrorEnum.UNSCHEDULE_JOB_ERROR, task.getId());
        }
        LOGGER.debug("unscheduleJob() - END");
    }

    private Trigger getCurrentTrigger(final Task task) {
        LOGGER.debug("getCurrentTrigger() - BEGIN: {}", task.getId());
        Trigger trigger = null;
        try {
            for (JobKey jobKey : this.quartzScheduler.getJobKeys(GroupMatcher.jobGroupEquals(JobsUtil.TASK_GROUP_NAME))) {
                if (jobKey.getName().equals(task.getJobId())) {
                    TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobId(), JobsUtil.TASK_GROUP_NAME);
                    trigger = this.quartzScheduler.getTrigger(triggerKey);
                }
            }
        } catch (SchedulerException e) {
            throw new QuartzException(e, SchedulerErrorEnum.GET_CURRENT_TRIGGER_ERROR, task.getId());
        }
        if (Objects.isNull(trigger)) {
            throw new ResourceNotFoundException(ResourceErrorEnum.TRIGGER_NOT_FOUND, task.getId());
        }
        LOGGER.debug("getTrigger() - END");
        return trigger;
    }
}
