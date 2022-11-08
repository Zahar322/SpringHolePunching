package com.socket.container;

import com.socket.api.FileData;
import com.socket.api.SocketData;
import com.socket.api.SocketRequest;
import com.socket.api.SocketResponse;
import com.socket.api.Target;
import com.socket.api.UdpRequest;
import com.socket.api.UdpResponse;
import com.socket.mapper.ObjectMapperWrapper;
import com.socket.model.domain.UdpSocketWrapper;
import com.socket.processor.Processor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.socket.common.Constant.Commands.ANNOUNCE;
import static com.socket.common.Constant.Commands.CONNECT;
import static com.socket.common.Constant.Commands.KEEP_ALIVE;
import static com.socket.common.Constant.Commands.P2P;
import static com.socket.common.Constant.Commands.SEND;
import static com.socket.common.Constant.MessageTypes.FILE;
import static com.socket.common.Constant.MessageTypes.SOUND;
import static com.socket.common.Constant.MessageTypes.STRING;
import static java.util.Optional.ofNullable;

@Slf4j
@Component
public class UdpSocketContainer {

    private final Map<Integer, UdpSocketWrapper> sockets;
    private final ObjectMapperWrapper objectMapper;
    private final Executor executor;
    private final String deviceId;
    private final Executor waitingExecutor;
    private final Map<String, Processor> processors;

    public UdpSocketContainer(ObjectMapperWrapper objectMapper, Map<String, Processor> processors) {
        this.objectMapper = objectMapper;
        this.sockets = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(20);
        this.deviceId = UUID.randomUUID().toString();
        this.waitingExecutor = Executors.newFixedThreadPool(20);
        this.processors = processors;
    }

    public SocketResponse createConnection(SocketRequest request) {
        SocketData socketData = request.getData();
        UdpSocketWrapper wrapper = createSocket(socketData);
        if (wrapper != null) {
            return createConnection(wrapper);
        }
        return null;
    }

    @SneakyThrows
    private UdpSocketWrapper createSocket(SocketData socketData) {
        try {
//            DatagramSocket socket = new DatagramSocket(null);
//            socket.setReuseAddress(true);
//            socket.bind(new InetSocketAddress(0));
            DatagramSocket socket = new DatagramSocket();
            return new UdpSocketWrapper(socket, socketData.getPort(), socketData.getHost());
        } catch (SocketException e) {
            log.error("Unexpected error cannot create socket ", e);
            return null;
        }
    }

    private SocketResponse createConnection(UdpSocketWrapper socket) {
        UdpRequest request = new UdpRequest(deviceId, CONNECT);
        byte [] data = objectMapper.writeAsBytes(request);
        if (data != null) {
            UdpResponse response = objectMapper.readBytes(socket.connect(data), UdpResponse.class);
            socket.setExternalPort(response.getPort());
            socket.setExternalHost(response.getHost());
            Integer localPort = socket.getLocalPort();
            sockets.put(localPort, socket);
            waitingExecutor.execute(() -> waiting(socket));
            return new SocketResponse(createData(response, localPort));
        }
        return null;
    }

    private SocketData createData(UdpResponse response, Integer localPort) {
        SocketData data = new SocketData();
        data.setHost(response.getHost());
        data.setPort(response.getPort());
        data.setInternalPort(localPort);
        return data;
    }

    public SocketResponse announce(SocketRequest socketRequest) {
        UdpSocketWrapper socket = sockets.get(socketRequest.getData().getInternalPort());
        byte[] request = objectMapper.writeAsBytes(new UdpRequest(deviceId, ANNOUNCE, socketRequest.getData().getFileNames()));
        byte[] response = socket.connect(request);
        UdpResponse udpResponse = objectMapper.readBytes(response, UdpResponse.class);
        return new SocketResponse(udpResponse.getDeviceId());
    }

    @Scheduled(fixedDelay = 30000)
    public void keepAlive() {
        sockets.values().forEach(this::sendKeepAliveMessage);
    }

    private void sendKeepAliveMessage(UdpSocketWrapper socket) {
        UdpRequest udpRequest = new UdpRequest(deviceId, KEEP_ALIVE);
        byte[] request = objectMapper.writeAsBytes(udpRequest);
        executor.execute(() -> socket.sendMessage(request));
    }

    private void waiting(UdpSocketWrapper socket) {
        boolean start = true;
        while (start) {
            UdpResponse response = objectMapper.readBytes(socket.receiveMessage(), UdpResponse.class);
            start = process(response, socket);
        }
    }

    private boolean process(UdpResponse response, UdpSocketWrapper socket) {
        return ofNullable(response).map(UdpResponse::getType)
                                   .map(processors::get)
                                   .map(processor -> processor.process(response, socket))
                                   .orElse(true);
    }

    public SocketResponse send(SocketRequest socketRequest) {
        UdpSocketWrapper socket = createSocket(socketRequest.getData());
        byte[] request = objectMapper.writeAsBytes(new UdpRequest(deviceId, SEND, socketRequest.getData().getFileNames(), socketRequest.getTargets()));
        socket.sendMessage(request);
//        UdpResponse udpResponse = objectMapper.readBytes(response, UdpResponse.class);
        return new SocketResponse(deviceId);
    }

    public SocketResponse p2pConnection(SocketRequest socketRequest) {
        UdpSocketWrapper socket = sockets.get(socketRequest.getData().getInternalPort());
        List<Target> targets = socketRequest.getTargets();
        byte[] request = objectMapper.writeAsBytes(new UdpRequest(P2P, targets));
        socket.sendMessage(request);
        p2pConnection(socket, targets.get(0));
        return new SocketResponse(deviceId);
    }

    private void p2pConnection(UdpSocketWrapper socket, Target target) {
        socket.changePort(target.getPort());
        socket.changeHost(target.getHost());
        Processor processor = processors.get(STRING);
        processor.sendConnectMessage(socket);
    }

    public SocketResponse p2pSend(SocketRequest socketRequest) {
        UdpSocketWrapper socket = sockets.get(socketRequest.getData().getInternalPort());
        byte[] request = objectMapper.writeAsBytes(new UdpResponse(STRING, P2P.getBytes()));
        socket.sendMessage(request);
        return new SocketResponse(deviceId);
    }

    @SneakyThrows
    public SocketResponse p2pSound(SocketRequest socketRequest) {
        UdpSocketWrapper socket = createSocket(socketRequest.getData().getInternalPort());
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        DataLine.Info dataInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataInfo);
        targetDataLine.open();
        byte[] data = new byte[targetDataLine.getBufferSize()];
        targetDataLine.start();
        for (int count = 0; count < 50; count++) {
            targetDataLine.read(data, 0, data.length);
            sendSound(data, socket);
        }
        targetDataLine.close();
        return new SocketResponse(deviceId);
    }

    @SneakyThrows
    private UdpSocketWrapper createSocket(Integer internalPort) {
        UdpSocketWrapper socketWrapper = sockets.get(internalPort);
        boolean create = true;
        if (create) {
            return socketWrapper;
        }
        DatagramSocket socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.setSoTimeout(1400);
        socket.bind(new InetSocketAddress(internalPort));
        return new UdpSocketWrapper(socket, socketWrapper.getPort(), socketWrapper.getHost());
    }

    private void sendSound(byte[] data, UdpSocketWrapper socket) {
        int offset = 256;
        int start = 0;
        int end = offset;
        for (int chunk = 0; chunk < data.length/offset; chunk++) {
            byte[] bytes = Arrays.copyOfRange(data, start, end);
            sendChunkSound(bytes, socket);
            start +=offset;
            end += offset;
        }
        byte[] bytes = Arrays.copyOfRange(data, start, data.length);
        sendChunkSound(bytes, socket);
    }

    private void sendChunkSound(byte[] data, UdpSocketWrapper socket) {
        byte[] request = objectMapper.writeAsBytes(new UdpResponse(SOUND, data));
        socket.sendMessage(request);
    }

    public SocketResponse p2pFiles(SocketRequest socketRequest) throws IOException {
        SocketData socketData = socketRequest.getData();
        UdpSocketWrapper socket = sockets.get(socketData.getInternalPort());
        File file = new File(socketData.getFileNames().get(0));
        byte[] data = FileUtils.readFileToByteArray(file);
        FileData fileData = new FileData(file.getName());
        sendFile(data, socket, fileData);
        return new SocketResponse(deviceId);
    }

    private void sendFile(byte[] data, UdpSocketWrapper socket, FileData fileData) {
        int offset = 256;
        int start = 0;
        int end = offset;
        for (int chunk = 0; chunk < data.length/offset; chunk++) {
            byte[] bytes = Arrays.copyOfRange(data, start, end);
            sendChunkFile(bytes, socket, fileData);
            start +=offset;
            end += offset;
            fileData.setOffset(chunk);
        }
        byte[] bytes = Arrays.copyOfRange(data, start, data.length);
        sendChunkFile(bytes, socket, fileData);
    }

    private void sendChunkFile(byte[] data, UdpSocketWrapper socket, FileData fileData) {
        UdpResponse response = new UdpResponse(FILE, data);
        response.setFileData(fileData);
        byte[] request = objectMapper.writeAsBytes(response);
        socket.sendMessage(request);
    }

//    private void waiting(UdpSocketWrapper socket) {
//        UdpRequest udpRequest = new UdpRequest(WAITING);
//        executor.execute(() -> socket.connect());
//    }
}
