package com.socket.processor;

import com.socket.api.UdpResponse;
import com.socket.mapper.ObjectMapperWrapper;
import com.socket.model.domain.UdpSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import static com.socket.common.Constant.Commands.CONNECT;

@Slf4j
public class StringProcessor extends Processor {

    public StringProcessor(ObjectMapperWrapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean process(UdpResponse response, UdpSocketWrapper socket) {
        String message = new String(response.getData());
        log(message, socket);
        if (CONNECT.equals(message)) {
            socket.changeTarget(true);
        }
        return true;
    }

    private void log(String message, UdpSocketWrapper socket) {
        log.info("receive message {} from host {} and port {}", message, socket.getHost(), socket.getPort());
    }
}
