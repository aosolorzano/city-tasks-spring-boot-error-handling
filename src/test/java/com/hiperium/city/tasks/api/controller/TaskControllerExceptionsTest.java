package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.common.AbstractContainerBase;
import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import com.hiperium.city.tasks.api.utils.enums.LanguageCodeEnum;
import com.hiperium.city.tasks.api.utils.enums.ResourceErrorEnum;
import com.hiperium.city.tasks.api.utils.enums.ValidationErrorEnum;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@AutoConfigureWebTestClient
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerExceptionsTest extends AbstractContainerBase {

    @Autowired
    private WebTestClient webTestClient;

    private static Task task;

    @BeforeAll
    public static void init() {
        task = TasksUtil.getTaskTemplateObject();
    }

    @Test
    @DisplayName("Find Tasks that does not exist")
    void givenNotExistingTasksId_whenFindTaskById_thenReturnError404() {
        task.setId(100L);
        this.webTestClient
                .get()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), task.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, LanguageCodeEnum.EN.getCode())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDetailsDTO.class)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(ResourceErrorEnum.TASK_NOT_FOUND.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task not found with ID: " + task.getId() + ".");
                });
    }

    @Test
    @DisplayName("Find Tasks that does not exist - Spanish")
    void givenNotExistingTasksId_whenFindTaskById_thenReturnError404InSpanish() {
        task.setId(101L);
        this.webTestClient
                .get()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), task.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, LanguageCodeEnum.ES.getCode())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDetailsDTO.class)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(ResourceErrorEnum.TASK_NOT_FOUND.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("No se encontró la tarea con ID: " + task.getId() + ".");
                });
    }

    @Test
    @DisplayName("Update Tasks that does not exist")
    void givenNotExistingTasksId_whenUpdateTask_thenReturnError404() {
        task.setId(200L);
        this.webTestClient
                .put()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), task.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, LanguageCodeEnum.EN.getCode())
                .bodyValue(task)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDetailsDTO.class)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(ResourceErrorEnum.TASK_NOT_FOUND.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task not found with ID: " + task.getId() + ".");
                });
    }

    @Test
    @DisplayName("Update Tasks that does not exist - Spanish")
    void givenNotExistingTasksId_whenUpdateTask_thenReturnError404InSpanis() {
        task.setId(201L);
        this.webTestClient
                .put()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), task.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, LanguageCodeEnum.ES.getCode())
                .bodyValue(task)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDetailsDTO.class)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(ResourceErrorEnum.TASK_NOT_FOUND.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("No se encontró la tarea con ID: " + task.getId() + ".");
                });
    }

    @Test
    @DisplayName("Delete not existing Task")
    void givenTaskId_whenDeleteTaskById_thenReturnError404() {
        task.setId(300L);
        this.webTestClient
                .delete()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), task.getId())
                .header(HttpHeaders.ACCEPT_LANGUAGE, LanguageCodeEnum.EN.getCode())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDetailsDTO.class)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(ResourceErrorEnum.TASK_NOT_FOUND.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task not found with ID: " + task.getId() + ".");
                });
    }

    @Test
    @DisplayName("Delete not existing Task - Spanish")
    void givenTaskId_whenDeleteTaskById_thenReturnError404InSpanish() {
        task.setId(300L);
        this.webTestClient
                .delete()
                .uri(TasksUtil.TASKS_PATH.concat("/{id}"), task.getId())
                .header(HttpHeaders.ACCEPT_LANGUAGE, LanguageCodeEnum.ES.getCode())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDetailsDTO.class)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(ResourceErrorEnum.TASK_NOT_FOUND.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("No se encontró la tarea con ID: " + task.getId() + ".");
                });
    }
}
