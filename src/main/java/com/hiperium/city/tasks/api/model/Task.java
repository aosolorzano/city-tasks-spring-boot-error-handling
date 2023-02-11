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

    @NotEmpty(message = "The name must not be empty.")
    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull(message = "The enabled flag must not be null.")
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "job_id", length = 30, nullable = false)
    private String jobId;

    @NotEmpty(message = "The device ID must not be empty.")
    @Column(name = "device_id", length = 30, nullable = false)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "The device action must not be null.")
    @Column(name = "device_action", length = 10, nullable = false)
    private DeviceActionEnum deviceAction;

    @Column(name = "device_execution_command", length = 90)
    private String deviceExecutionCommand;

    @Min(value = 0, message = "The hour must be greater than or equal to 0.")
    @Max(value = 23, message = "The hour must be less than or equal to 23.")
    @Column(name = "task_hour", nullable = false)
    private int hour;

    @Min(value = 0, message = "The minute must be greater than or equal to 0.")
    @Max(value = 59, message = "The minute must be less than or equal to 59.")
    @Column(name = "task_minute", nullable = false)
    private int minute;

    @NotEmpty(message = "The execution days must not be empty.")
    @Column(name = "execution_days", length = 30, nullable = false)
    private String executionDays;

    @Future(message = "The execute until date must be in the future.")
    @Column(name = "execute_until")
    private ZonedDateTime executeUntil;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}
