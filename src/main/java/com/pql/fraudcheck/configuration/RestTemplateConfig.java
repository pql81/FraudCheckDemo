package com.pql.fraudcheck.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Configuration
public class RestTemplateConfig {

    @Value("${rest.template.connection.timeout.millis}")
    private int connTimeoutMillis;
    @Value("${rest.template.read.timeout.millis}")
    private int readTimeoutMillis;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        return builder
                .setConnectTimeout(Duration.ofMillis(connTimeoutMillis))
                .setReadTimeout(Duration.ofMillis(readTimeoutMillis))
                .build();
    }
}
