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

import static com.socket.common.Constant.MessageTypes.TARGET;

public class P2PConnectCommand extends Command {

    public P2PConnectCommand(ObjectMapperWrapper objectMapper, Map<Target, Connection> connections) {
        super(objectMapper, connections);
    }

    @Override
    public void execute(DatagramSocket socket, DatagramPacket packet, UdpRequest request) {
        Target target = new Target(packet);
        List<Connection> connections = getConnectionsByTargets(request.getConnectedTargets());
        byte[] data = objectMapper.writeAsBytes(target);
        UdpResponse udpResponse = new UdpResponse(TARGET, data);
        byte[] response = objectMapper.writeAsBytes(udpResponse);
        connections.forEach(connection -> sendMessage(socket, response, connection));
        packet.setData(response);
    }
}
