package com.codeit.aws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")      // 모든 HTTP 메서드 허용
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);            // Preflight 캐시 1시간
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // dev 로컬 파일 서빙: /attachments/** → .aws-test/storage/
        registry.addResourceHandler("/attachments/**")
                .addResourceLocations("file:.aws-test/storage/");
    }
}
