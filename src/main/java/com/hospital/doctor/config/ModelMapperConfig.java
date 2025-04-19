package com.hospital.doctor.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // String -> LocalDate Converter
        Converter<String, LocalDate> stringToLocalDate = new Converter<>() {
            @Override
            public LocalDate convert(MappingContext<String, LocalDate> context) {
                return context.getSource() == null ? null : LocalDate.parse(context.getSource());
            }
        };

        modelMapper.createTypeMap(String.class, LocalDate.class);
        modelMapper.addConverter(stringToLocalDate);

        return modelMapper;
    }
}
