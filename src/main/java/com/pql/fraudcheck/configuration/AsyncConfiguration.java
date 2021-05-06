package com.pql.fraudcheck.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
        return executor;
    }
}
