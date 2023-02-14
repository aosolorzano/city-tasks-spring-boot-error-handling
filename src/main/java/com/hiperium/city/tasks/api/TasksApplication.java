package com.hiperium.city.tasks.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hiperium.city.tasks.api.config.PropertiesLoader;
import com.hiperium.city.tasks.api.config.hints.MessagesRuntimeHints;
import com.hiperium.city.tasks.api.config.hints.PostgresRuntimeHints;
import com.hiperium.city.tasks.api.config.hints.QuartzRuntimeHints;
import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import com.hiperium.city.tasks.api.job.TaskJob;
import com.hiperium.city.tasks.api.vo.AuroraPostgresSecretVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

@SpringBootApplication
@ImportRuntimeHints({PostgresRuntimeHints.class, QuartzRuntimeHints.class, MessagesRuntimeHints.class})
@RegisterReflectionForBinding({AuroraPostgresSecretVO.class, TaskJob.class, ErrorDetailsDTO.class})
public class TasksApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(TasksApplication.class);

    public static void main(String[] args) throws JsonProcessingException {
        LOGGER.info("main() - BEGIN");
        PropertiesLoader.load();
        SpringApplication.run(TasksApplication.class, args);
        LOGGER.info("main() - END");
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding(StandardCharsets.ISO_8859_1.name());
        return messageSource;
    }
}
