package com.socket.server;

import com.socket.api.UdpRequest;
import com.socket.server.command.Command;
import com.socket.server.model.Connection;
import com.socket.api.Target;
import com.socket.server.wrapper.ObjectMapperWrapper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UdpServer {

    private final int port;
    private final Executor executor;
    private final ObjectMapperWrapper objectMapper;
    private final Map<String, Command> commands;
    private final Map<Target, Connection> connections;

    public UdpServer(ObjectMapperWrapper objectMapper,
                     Map<String, Command> commands,
                     Map<Target, Connection> connections) {
        this.port = 7900;
        this.executor = Executors.newFixedThreadPool(50);
        this.objectMapper = objectMapper;
        this.commands = commands;
        this.connections = connections;
    }

    public void start() throws IOException {
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("server starts with port " + port);
        while (true) {
            byte[] request = new byte[300];
            DatagramPacket packet = new DatagramPacket(request, request.length);
            socket.receive(packet);
            executor.execute(() -> send(socket, packet));
        }
    }

    private void send(DatagramSocket socket, DatagramPacket packet) {
        try {
            byte[] data = packet.getData();
            UdpRequest request = objectMapper.readBytes(data, UdpRequest.class);
            System.out.println(objectMapper.writeAsString(request));
            Command command = commands.get(request.getCommand());
            command.execute(socket, packet, request);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
