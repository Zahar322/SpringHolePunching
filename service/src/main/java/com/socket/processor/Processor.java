package com.socket.processor;

import com.socket.api.UdpResponse;
import com.socket.mapper.ObjectMapperWrapper;
import com.socket.model.domain.UdpSocketWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.socket.common.Constant.Commands.CONNECT;
import static com.socket.common.Constant.MessageTypes.STRING;

@Slf4j
@RequiredArgsConstructor
public abstract class Processor {

    protected final ObjectMapperWrapper objectMapper;

    public abstract boolean process(UdpResponse response, UdpSocketWrapper socket);

    public void sendConnectMessage(UdpSocketWrapper socket) {
        boolean start = true;
        while (start) {
            UdpResponse response = new UdpResponse(STRING, CONNECT.getBytes());
            log.info("send connect message to host {} and port {}", socket.getHost(), socket.getPort());
            byte[] data = objectMapper.writeAsBytes(response);
            socket.sendMessage(data);
            start = !socket.isChangeTarget();
            sleep();
        }
    }

//    public void sendConnectMessage(UdpSocketWrapper socket) {
//        for (int attempt = 0; attempt < 30; attempt++) {
//            UdpResponse response = new UdpResponse(STRING, CONNECT.getBytes());
//            log.info("send connect message to host {} and port {}", socket.getHost(), socket.getPort());
//            byte[] data = objectMapper.writeAsBytes(response);
//            socket.sendMessage(data);
//            if (socket.isChangeTarget()) {
//                break;
//            }
//            sleep();
//        }
//    }

    private void sleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            log.error("Unexpected error ", e);
        }
    }
}
