package com.socket.server.command;

import com.socket.api.UdpRequest;
import com.socket.api.UdpResponse;
import com.socket.server.model.Connection;
import com.socket.api.Target;
import com.socket.server.wrapper.ObjectMapperWrapper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class ConnectCommand extends Command {

    public ConnectCommand(ObjectMapperWrapper objectMapper, Map<Target, Connection> connections) {
        super(objectMapper, connections);
    }

    @Override
    public void execute(DatagramSocket socket, DatagramPacket packet, UdpRequest request) {
        Target target = new Target(packet);
        String clientId = request.getClientId();
        Connection connection = new Connection(clientId, target);
        connection.setDatagramPacket(packet);
        connections.put(target, connection);
        UdpResponse udpResponse = new UdpResponse(clientId, target.getPort(), target.getHost());
        packet.setData(objectMapper.writeAsBytes(udpResponse));
        System.out.println("successfully create connection, host is " + udpResponse.getHost() + " " + "port is " + udpResponse.getPort());
    }
}
