package com.pql.fraudcheck.configuration;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Value("${thread.pool.core.size}")
    private int coreSize;
    @Value("${thread.pool.max.size}")
    private int maxSize;

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);

        // set the request Id to threads for better debugging
        executor.setTaskDecorator(new TaskDecorator() {
            @Override
            public Runnable decorate(Runnable runnable) {
                Map<String, String> webThreadContext = MDC.getCopyOfContextMap();
                return () -> {
                    try {
                        MDC.setContextMap(webThreadContext);
                        runnable.run();
                    } finally {
                        MDC.clear();
                    }
                };
            }
        });

        return executor;
    }
}
