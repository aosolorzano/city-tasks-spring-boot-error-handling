package com.hiperium.city.tasks.api.model;

import com.hiperium.city.tasks.api.utils.enums.DeviceActionEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HIP_CTY_TASKS")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HIP_CTY_TASKS_SEQ")
    @SequenceGenerator(name = "HIP_CTY_TASKS_SEQ", sequenceName = "HIP_CTY_TASKS_SEQ", allocationSize = 1)
    private Long id;

    @NotEmpty(message = "validation.task.name.NotEmpty.message")
    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull(message = "validation.task.enabled.NotEmpty.message")
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "job_id", length = 30, nullable = false)
    private String jobId;

    @NotEmpty(message = "validation.device.id.NotEmpty.message")
    @Column(name = "device_id", length = 30, nullable = false)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "validation.device.action.NotEmpty.message")
    @Column(name = "device_action", length = 10, nullable = false)
    private DeviceActionEnum deviceAction;

    @Column(name = "device_execution_command", length = 90)
    private String deviceExecutionCommand;

    @Min(value = 0, message = "validation.task.hour.Min.message")
    @Max(value = 23, message = "validation.task.hour.Max.message")
    @Column(name = "task_hour", nullable = false)
    private int hour;

    @Min(value = 0, message = "validation.task.minute.Min.message")
    @Max(value = 59, message = "validation.task.minute.Max.message")
    @Column(name = "task_minute", nullable = false)
    private int minute;

    @NotEmpty(message = "validation.task.execution.days.NotEmpty.message")
    @Column(name = "execution_days", length = 30, nullable = false)
    private String executionDays;

    @Future(message = "validation.task.execute.until.Future.message")
    @Column(name = "execute_until")
    private ZonedDateTime executeUntil;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}
