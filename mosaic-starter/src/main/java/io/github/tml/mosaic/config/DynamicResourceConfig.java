package io.github.tml.mosaic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Configuration
public class DynamicResourceConfig {

    @Bean
    public SimpleUrlHandlerMapping dynamicResourceHandlerMapping() {
        return new SimpleUrlHandlerMapping();
    }

    @Bean
    public ResourceHttpRequestHandler dynamicResourceHandler() {
        return new ResourceHttpRequestHandler();
    }
}