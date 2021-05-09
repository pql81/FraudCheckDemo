package com.pql.fraudcheck.configuration;

import lombok.extern.log4j.Log4j2;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.time.Duration;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Log4j2
@Configuration
public class RestTemplateConfig {

    @Value("${rest.template.connection.timeout.millis}")
    private int connTimeoutMillis;
    @Value("${rest.template.read.timeout.millis}")
    private int readTimeoutMillis;
    @Value("${client.ssl.trust-store-password:}")
    private String trustStorePassword;
    @Value("${client.ssl.trust-store:}")
    private Resource trustStore;
    @Value("${client.ssl.key-store-password:}")
    private String keyStorePassword;
    @Value("${client.ssl.key-password:}")
    private String keyPassword;
    @Value("${client.ssl.key-store:}")
    private Resource keyStore;


    @Bean
    @Profile("!mtls")
    public RestTemplate restTemplate(RestTemplateBuilder builder) throws Exception {

        return getRestTemplate(builder);
    }

    @Bean
    @Profile("mtls")
    public RestTemplate mtlsRestTemplate(RestTemplateBuilder builder) throws Exception {

        RestTemplate restTemplate = getRestTemplate(builder);
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(getHttpClient()));

        return restTemplate;
    }

    private RestTemplate getRestTemplate(RestTemplateBuilder builder) throws Exception {
        return builder
                .setConnectTimeout(Duration.ofMillis(connTimeoutMillis))
                .setReadTimeout(Duration.ofMillis(readTimeoutMillis))
                .build();
    }

    // service client SSL configuration - when MTLS is enabled, it uses keystore and truststore in resources client folder
    private HttpClient getHttpClient() throws Exception {
        log.info("Initializing SSLContext for MTLS");

        SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(
                trustStore.getFile(),
                trustStorePassword.toCharArray())
                .loadKeyMaterial(keyStore.getFile(),
                        keyStorePassword.toCharArray(),
                        keyPassword.toCharArray())
                .build();

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier());

        return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
    }
}
