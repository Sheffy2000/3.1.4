package ru.kata.spring.boot_security.demo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kata.spring.boot_security.demo.model.UserDTO;

import static org.springframework.beans.factory.support.InstanceSupplier.using;

@Configuration
public class ModelMappingConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
