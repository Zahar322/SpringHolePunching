package com.socket.service;

import com.socket.container.UdpSocketContainer;
import com.socket.api.SocketRequest;
import com.socket.api.SocketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SocketService {

    private final UdpSocketContainer container;

    public SocketResponse createConnection(SocketRequest socketRequest) {
        return container.createConnection(socketRequest);
    }

    public SocketResponse announce(SocketRequest socketRequest) {
        return container.announce(socketRequest);
    }

    public SocketResponse send(SocketRequest socketRequest) {
        return container.send(socketRequest);
    }

    public SocketResponse p2pConnection(SocketRequest socketRequest) {
        return container.p2pConnection(socketRequest);
    }

    public SocketResponse p2pSend(SocketRequest socketRequest) {
        return container.p2pSend(socketRequest);
    }

    public SocketResponse p2pSound(SocketRequest socketRequest) {
        return container.p2pSound(socketRequest);
    }

    public SocketResponse p2pFiles(SocketRequest socketRequest) throws IOException {
        return container.p2pFiles(socketRequest);
    }
}
