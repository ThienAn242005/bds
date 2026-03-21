package com.homeverse.identity.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // Khai báo Bean này để UserMapper có thể @Autowired được
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}