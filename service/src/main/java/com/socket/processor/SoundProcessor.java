package com.socket.processor;

import com.socket.api.UdpResponse;
import com.socket.mapper.ObjectMapperWrapper;
import com.socket.model.domain.UdpSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.SourceDataLine;
import java.util.Arrays;

@Slf4j
public class SoundProcessor extends Processor {

    public SoundProcessor(ObjectMapperWrapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean process(UdpResponse response, UdpSocketWrapper socket) {
        SourceDataLine sourceDataLine = socket.getSourceDataLine();
        if (sourceDataLine == null) {
            sourceDataLine = socket.createSourceDataLine();
        }
        log.info(Arrays.toString(response.getData()));
        sourceDataLine.write(response.getData(), 0, response.getData().length);
        return true;
    }
}
