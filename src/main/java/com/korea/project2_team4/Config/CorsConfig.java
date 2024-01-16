package com.korea.project2_team4.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowedOriginPatterns("*");

//                .allowedOrigins("http://localhost:8888") //클라이언트 도메인 변경 시 수정하기
//                .allowedMethods("GET","POST","PUT","DELETE")
//                .allowCredentials(true)
//                .maxAge(3600);
    }
}
