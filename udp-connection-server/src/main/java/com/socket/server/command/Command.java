package com.socket.server.command;

import com.socket.api.UdpRequest;
import com.socket.server.model.Connection;
import com.socket.api.Target;
import com.socket.server.wrapper.ObjectMapperWrapper;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public abstract class Command {

    protected final ObjectMapperWrapper objectMapper;
    protected final Map<Target, Connection> connections;

    public abstract void execute(DatagramSocket socket, DatagramPacket packet, UdpRequest request);

    public List<Connection> getConnectionsByTargets(List<Target> targets) {
        return targets.stream()
                      .map(connections::get)
                      .filter(Objects::nonNull)
                      .collect(toList());
    }

    public void sendMessage(DatagramSocket socket, byte[] data, Connection connection) {
        DatagramPacket packet = connection.getDatagramPacket();
        Target target = connection.getTarget();
        packet.setData(data);
        try {
            socket.send(packet);
            System.out.println("successfully send message to host " + target.getHost() + " port " + target.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
