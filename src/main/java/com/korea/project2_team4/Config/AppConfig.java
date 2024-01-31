package com.korea.project2_team4.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    static String getImageFileDirPath()
    {
        return System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\";
//        return "c:\\files\\";
    }
}
