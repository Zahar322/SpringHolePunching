package com.socket.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UdpRequest implements Serializable {

    private String clientId;
    private String command;
    private List<String> fileNames;
    private List<Target> connectedTargets;

    public UdpRequest(String clientId, String command) {
        this.clientId = clientId;
        this.command = command;
    }

    public UdpRequest(String clientId, String command, List<String> fileNames) {
        this.clientId = clientId;
        this.command = command;
        this.fileNames = fileNames;
    }

    public UdpRequest(String command, List<Target> connectedTargets) {
        this.command = command;
        this.connectedTargets = connectedTargets;
    }

    public List<String> getFileNames() {
        if (fileNames == null) {
            fileNames = new ArrayList<>();
        }
        return fileNames;
    }
}
