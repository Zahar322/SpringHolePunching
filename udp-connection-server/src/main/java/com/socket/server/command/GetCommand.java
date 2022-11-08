package com.socket.server.command;

import com.socket.api.Target;
import com.socket.api.UdpRequest;
import com.socket.server.model.Connection;
import com.socket.server.wrapper.ObjectMapperWrapper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class GetCommand extends Command {

    public GetCommand(ObjectMapperWrapper objectMapper, Map<Target, Connection> connections) {
        super(objectMapper, connections);
    }

    @Override
    public void execute(DatagramSocket socket, DatagramPacket packet, UdpRequest request) {

    }
}
