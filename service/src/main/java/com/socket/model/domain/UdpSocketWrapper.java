package com.socket.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UdpSocketWrapper {

    private DatagramSocket socket;
    private String externalHost;
    private int externalPort;
    private SourceDataLine sourceDataLine;
    private volatile int port;
    private volatile String host;
    private volatile boolean changeTarget;
    private Map<String, Integer> filesChunks = new ConcurrentHashMap<>();

    public UdpSocketWrapper(DatagramSocket socket, int port, String host) {
        this.socket = socket;
        this.port = port;
        this.host = host;
    }

    public byte[] connect(byte[] data) {
        sendMessage(data);
        return receiveMessage();
    }

    public void sendMessage(byte[] data) {
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(host), port);
            socket.send(packet);
        } catch (IOException e) {
           log.error("Unexpected error, cannot send packet ", e);
        }
    }

    public byte[] receiveMessage() {
        int size = 512;
        byte[] response = new byte[size];
        DatagramPacket packet = new DatagramPacket(response, response.length);
        long time = System.currentTimeMillis();
        try {
            socket.receive(packet);
        } catch (IOException e) {
            log.error("Unexpected error, fail receive data {} ms ", System.currentTimeMillis() - time);
            return null;
        }
        return packet.getData();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public synchronized void changePort(int port) {
        this.port = port;
    }

    public synchronized void changeHost(String host) {
        this.host = host;
    }

    public synchronized void changeTarget(boolean changeTarget) {
        this.changeTarget = changeTarget;
    }

    @SneakyThrows
    public synchronized SourceDataLine createSourceDataLine() {
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        DataLine.Info dataInfo = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataInfo);
        this.sourceDataLine = sourceDataLine;
        sourceDataLine.open();
        sourceDataLine.start();
        return sourceDataLine;
    }

    public synchronized SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }

    public void closeSourceDataLine() {
        sourceDataLine.close();
        sourceDataLine = null;
    }

    public Map<String, Integer> getFilesChunks() {
        return filesChunks;
    }

    public boolean isValidChunk(String fileName, int offset) {
        Integer currentOffset = getFilesChunks().get(fileName);
        return currentOffset == null || offset - currentOffset == 1;
    }
}
