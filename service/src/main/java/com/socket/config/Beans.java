package com.socket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socket.mapper.ObjectMapperWrapper;
import com.socket.processor.ChunkProcessor;
import com.socket.processor.FileProcessor;
import com.socket.processor.Processor;
import com.socket.processor.SoundProcessor;
import com.socket.processor.StringProcessor;
import com.socket.processor.TargetProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.socket.common.Constant.MessageTypes.CHUNK;
import static com.socket.common.Constant.MessageTypes.FILE;
import static com.socket.common.Constant.MessageTypes.SOUND;
import static com.socket.common.Constant.MessageTypes.STRING;
import static com.socket.common.Constant.MessageTypes.TARGET;
import static com.socket.common.MapperUtils.getObjectMapper;

@Configuration
public class Beans {

    @Bean
    public ObjectMapper objectMapper() {
        return getObjectMapper();
    }

    @Bean
    public Map<String, Processor> processors(ObjectMapperWrapper objectMapper) {
        Map<String, Processor> processors = new HashMap<>();
        processors.put(TARGET, new TargetProcessor(objectMapper));
        processors.put(STRING, new StringProcessor(objectMapper));
        processors.put(SOUND, new SoundProcessor(objectMapper));
        processors.put(FILE, new FileProcessor(objectMapper));
        processors.put(CHUNK, new ChunkProcessor(objectMapper));
        return processors;
    }
}
