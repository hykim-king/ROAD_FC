package com.pcwk.ehr;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")

                .allowedOrigins("http://localhost:8080") // 프론트엔드 도메인 설정
                .allowedMethods("GET", "POST", "PUT", "DELETE")

                .allowedOrigins("http://example.com", "http://anotherdomain.com")  // 특정 출처 명시
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

