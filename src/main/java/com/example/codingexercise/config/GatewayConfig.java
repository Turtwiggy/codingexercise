package com.example.codingexercise.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GatewayConfig {

    @Value("${external.api.username}")
    private String username;

    @Value("${external.api.password}")
    private String password;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    @Qualifier("product_auth")
    public RestTemplate restTemplateWithAuth() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor(username, password));

        return restTemplate;
    }

}
