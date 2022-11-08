package com.socket.server.command;

import com.socket.api.UdpRequest;
import com.socket.api.UdpResponse;
import com.socket.server.model.Connection;
import com.socket.api.Target;
import com.socket.server.wrapper.ObjectMapperWrapper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class AnnounceCommand extends Command {

    public AnnounceCommand(ObjectMapperWrapper objectMapper, Map<Target, Connection> connections) {
        super(objectMapper, connections);
    }

    @Override
    public void execute(DatagramSocket socket, DatagramPacket packet, UdpRequest request) {
        Target target = new Target(packet);
        Connection connection = connections.get(target);
        connection.setFileNames(request.getFileNames());
        UdpResponse response = new UdpResponse(connection.getClientId(), target.getPort(), target.getHost());
        packet.setData(objectMapper.writeAsBytes(response));
    }
}
