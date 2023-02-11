package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.service.TaskService;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(TasksUtil.TASKS_PATH)
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Task> create(@RequestBody @Valid Task newTask) {
        LOGGER.debug("create(): {}", newTask);
        return this.taskService.create(newTask);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Task> update(@PathVariable("id") long taskId, @RequestBody @Valid Task updatedTask) {
        LOGGER.debug("update(): {}", updatedTask);
        return this.taskService.update(taskId, updatedTask);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable("id") long taskId) {
        LOGGER.debug("delete(): {}", taskId);
        return this.taskService.delete(taskId);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Task> findById(@PathVariable("id") long taskId) {
        LOGGER.debug("findById(): {}", taskId);
        return this.taskService.findById(taskId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<Task> getAll() {
        LOGGER.debug("getAll() - START");
        return this.taskService.findAll();
    }

    @GetMapping("/template")
    @ResponseStatus(HttpStatus.OK)
    public Task getTaskTemplate() {
        return TasksUtil.getTaskTemplateObject();
    }
}
