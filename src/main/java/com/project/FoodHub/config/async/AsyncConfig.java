package com.project.FoodHub.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

// TODO: implement async config for request or processing tasks
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10); // tareas ligeras concurrentes
        executor.setMaxPoolSize(30); // tareas pesadas en paralelo
        executor.setQueueCapacity(100); // cola para ejecucion de tareas pesadas
        executor.setThreadNamePrefix("Async-");
        executor.setKeepAliveSeconds(60); // mantiene hilos activos
        executor.initialize();
        return executor;
    }
}
