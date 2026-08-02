package br.com.coretech.hero_api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        // Quando o Async for chamado, o java não vai pegar recursos da máquina, vai usar Virtual Thread
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
