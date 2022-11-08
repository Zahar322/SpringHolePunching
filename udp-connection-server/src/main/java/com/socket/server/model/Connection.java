package com.socket.server.model;

import com.socket.api.Target;
import lombok.Getter;
import lombok.Setter;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Connection {

    private String clientId;
    private Target target;
    private List<String> fileNames;
    private DatagramPacket datagramPacket;

    public Connection() {
    }

    public Connection(String clientId, Target target) {
        this.clientId = clientId;
        this.target = target;
    }

    public List<String> getFileNames() {
        if (fileNames == null) {
            fileNames = new ArrayList<>();
        }
        return fileNames;
    }

    public synchronized DatagramPacket getDatagramPacket() {
        return datagramPacket;
    }

    public synchronized void setDatagramPacket(DatagramPacket datagramPacket) {
        this.datagramPacket = datagramPacket;
    }
}
