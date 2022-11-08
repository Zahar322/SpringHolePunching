package com.socket.controller;

import com.socket.api.SocketData;
import com.socket.api.SocketRequest;
import com.socket.api.SocketResponse;
import com.socket.service.SocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/socket", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SocketController {

    private final SocketService socketService;

    @GetMapping("/{host}/{port}")
    public SocketResponse createConnection(@PathVariable String host, @PathVariable Integer port) {
        SocketRequest socketRequest = new SocketRequest();
        SocketData socketData = new SocketData();
        socketData.setPort(port);
        socketData.setHost(host);
        socketRequest.setData(socketData);
        return socketService.createConnection(socketRequest);
    }

    @PostMapping
    public SocketResponse createConnection(@RequestBody SocketRequest socketRequest) {
        return socketService.createConnection(socketRequest);
    }

    @PostMapping("/announce")
    public SocketResponse announce(@RequestBody SocketRequest socketRequest) {
        return socketService.announce(socketRequest);
    }

    @PostMapping("/send")
    public SocketResponse send(@RequestBody SocketRequest socketRequest) {
        return socketService.send(socketRequest);
    }

    @PostMapping("/p2p")
    public SocketResponse p2pConnection(@RequestBody SocketRequest socketRequest) {
        return socketService.p2pConnection(socketRequest);
    }

    @PostMapping("/p2p/send")
    public SocketResponse p2pSend(@RequestBody SocketRequest socketRequest) {
        return socketService.p2pSend(socketRequest);
    }

    @PostMapping("/p2p/sound")
    public SocketResponse p2pSound(@RequestBody SocketRequest socketRequest) {
        return socketService.p2pSound(socketRequest);
    }

    @PostMapping("/p2p/files")
    public SocketResponse p2pFiles(@RequestBody SocketRequest socketRequest) throws IOException {
        return socketService.p2pFiles(socketRequest);
    }
}
