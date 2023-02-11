package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.utils.enums.DeviceActionEnum;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public final class TasksUtil {

    public static final String TASKS_PATH = "/api/tasks";
    private static final List<String> DAYS_LIST = Arrays.asList("SUN","MON","TUE","WED","THU","FRI","SAT");
    private static final char[] HEX_ARRAY = "HiperiumTasksService".toCharArray();
    private static final int JOB_ID_LENGTH = 20;

    private TasksUtil() {
        // Empty constructor.
    }

    public static String generateJobId() {
        MessageDigest salt;
        try {
            salt = MessageDigest.getInstance("SHA-256");
            salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
        String uuid = bytesToHex(salt.digest());
        return uuid.substring(0, JOB_ID_LENGTH);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static Task getTaskTemplateObject() {
        return Task.builder()
                .name("Task name")
                .description("Task description")
                .enabled(true)
                .deviceId("123")
                .deviceAction(DeviceActionEnum.ACTIVATE)
                .deviceExecutionCommand("python /home/pi/hiperium/line_follower.py")
                .hour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .minute(Calendar.getInstance().get(Calendar.MINUTE))
                .executionDays(DAYS_LIST.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1))
                .executeUntil(ZonedDateTime.now().plusYears(1))
                .build();
    }
}
