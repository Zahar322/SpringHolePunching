package com.socket.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ObjectMapperWrapper {

    private final ObjectMapper objectMapper;

    public byte[] writeAsBytes(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            log.error("Unexpected error, cannot write object ", e);
            return null;
        }
    }

    public <T> T readBytes(byte[] bytes, Class<T> type) {
        try {
            return objectMapper.readValue(bytes, type);
        } catch (IOException e) {
            log.error("Unexpected error, cannot read value ", e);
            return null;
        }
    }
}
