package com.socket.server.command;

import com.socket.api.UdpRequest;
import com.socket.server.model.Connection;
import com.socket.api.Target;
import com.socket.server.wrapper.ObjectMapperWrapper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class KeepAliveCommand extends Command {

    public KeepAliveCommand(ObjectMapperWrapper objectMapper, Map<Target, Connection> connections) {
        super(objectMapper, connections);
    }

    @Override
    public void execute(DatagramSocket socket, DatagramPacket packet, UdpRequest request) {
        Target target = new Target(packet);
        Connection connection = connections.get(target);
        if (connection != null) {
            connection.setDatagramPacket(packet);
            System.out.println("connection with host " + target.getHost() + " and port " + target.getPort() + " is alive");
        }
    }
}
