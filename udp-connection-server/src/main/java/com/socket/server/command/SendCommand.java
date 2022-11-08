package com.socket.server.command;

import com.socket.api.Target;
import com.socket.api.UdpRequest;
import com.socket.api.UdpResponse;
import com.socket.server.model.Connection;
import com.socket.server.wrapper.ObjectMapperWrapper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Map;

public class SendCommand extends Command {

    public SendCommand(ObjectMapperWrapper objectMapper, Map<Target, Connection> connections) {
        super(objectMapper, connections);
    }

    @Override
    public void execute(DatagramSocket socket, DatagramPacket packet, UdpRequest request) {
        Target target = new Target(packet);
        UdpResponse udpResponse = new UdpResponse(request.getClientId(), target.getPort(), target.getHost());
        byte[] data = objectMapper.writeAsBytes(udpResponse);
        List<Connection> connections = getConnectionsByTargets(request.getConnectedTargets());
        for (Connection connection : connections) {
            sendMessage(socket, data, connection);
        }
    }
}
