package com.tutoras.tutoras.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Регистрация модуля для работы с Java 8 Date/Time API
        objectMapper.registerModule(new JavaTimeModule());
        
        // Отключение сериализации дат как числовых timestamp
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        // Игнорирование неизвестных свойств при десериализации
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Регистрация кастомного десериализатора для OffsetDateTime
        SimpleModule dateModule = new SimpleModule();
        dateModule.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
            private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            
            @Override
            public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String dateStr = p.getText();
                
                try {
                    // Сначала пробуем стандартный формат ISO
                    return OffsetDateTime.parse(dateStr, FORMATTER);
                } catch (Exception e) {
                    // Если не получается, проверяем другие форматы
                    try {
                        // Если просто дата (yyyy-MM-dd)
                        if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            LocalDate localDate = LocalDate.parse(dateStr);
                            return localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
                        }
                        // Если дата и время без смещения
                        else if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?")) {
                            LocalDateTime localDateTime = LocalDateTime.parse(dateStr);
                            return localDateTime.atOffset(ZoneOffset.UTC);
                        }
                    } catch (Exception inner) {
                        // Если и это не сработало, выбрасываем исключение
                        throw new IOException("Unable to parse date: " + dateStr, inner);
                    }
                    
                    throw new IOException("Unable to parse date: " + dateStr, e);
                }
            }
        });
        
        objectMapper.registerModule(dateModule);
        
        return objectMapper;
    }
} 