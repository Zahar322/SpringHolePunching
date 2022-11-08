package com.socket.server;

import com.socket.server.command.Command;
import com.socket.server.command.ConnectCommand;
import com.socket.server.command.KeepAliveCommand;
import com.socket.server.command.P2PConnectCommand;
import com.socket.server.command.SendCommand;
import com.socket.server.model.Connection;
import com.socket.api.Target;
import com.socket.server.wrapper.ObjectMapperWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.socket.common.Constant.Commands.CONNECT;
import static com.socket.common.Constant.Commands.KEEP_ALIVE;
import static com.socket.common.Constant.Commands.P2P;
import static com.socket.common.Constant.Commands.SEND;
import static com.socket.common.MapperUtils.getObjectMapper;

public class UdpServerLauncher {

    public static void main(String[] args) throws IOException {
        Map<Target, Connection> connections = new ConcurrentHashMap<>();
        ObjectMapperWrapper wrapper = new ObjectMapperWrapper(getObjectMapper());
        UdpServer server = new UdpServer(wrapper, getCommands(wrapper, connections), connections);
        server.start();
    }

    private static Map<String, Command> getCommands(ObjectMapperWrapper wrapper, Map<Target, Connection> connections) {
        Map<String, Command> commands = new HashMap<>();
        commands.put(CONNECT, new ConnectCommand(wrapper, connections));
        commands.put(KEEP_ALIVE, new KeepAliveCommand(wrapper, connections));
        commands.put(SEND, new SendCommand(wrapper, connections));
        commands.put(P2P, new P2PConnectCommand(wrapper, connections));
        return commands;
    }
}
