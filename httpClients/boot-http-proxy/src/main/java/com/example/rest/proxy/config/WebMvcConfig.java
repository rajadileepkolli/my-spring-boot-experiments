package com.example.rest.proxy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class WebMvcConfig implements WebMvcConfigurer {
    private final ApplicationProperties properties;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        ApplicationProperties.Cors propertiesCors = properties.getCors();
        registry.addMapping(propertiesCors.getPathPattern())
                .allowedMethods(propertiesCors.getAllowedMethods())
                .allowedHeaders(propertiesCors.getAllowedHeaders())
                .allowedOriginPatterns(propertiesCors.getAllowedOriginPatterns())
                .allowCredentials(propertiesCors.isAllowCredentials());
    }
}
