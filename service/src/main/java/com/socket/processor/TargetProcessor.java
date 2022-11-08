package com.socket.processor;

import com.socket.api.Target;
import com.socket.api.UdpResponse;
import com.socket.mapper.ObjectMapperWrapper;
import com.socket.model.domain.UdpSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public class TargetProcessor extends Processor {

    private final Executor executor;

    public TargetProcessor(ObjectMapperWrapper objectMapper) {
        super(objectMapper);
        this.executor = Executors.newFixedThreadPool(1);
    }

    @Override
    public boolean process(UdpResponse response, UdpSocketWrapper socket) {
        Target target = objectMapper.readBytes(response.getData(), Target.class);
        socket.changeHost(target.getHost());
        socket.changePort(target.getPort());
        log(socket);
        executor.execute(() -> sendConnectMessage(socket));
        return true;
    }

    private void log(UdpSocketWrapper socket) {
        log.info("change host to {} and port to {}", socket.getHost(), socket.getPort());
    }
}
