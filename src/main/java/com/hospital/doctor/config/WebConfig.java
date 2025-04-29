package com.hospital.doctor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow all origins, you can modify this to limit to specific domains like "http://localhost:3000"
        registry.addMapping("/**").allowedOrigins("http://localhost:5174").allowedMethods("GET", "POST", "PUT", "DELETE");
    }

}
