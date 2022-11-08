package com.socket.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UdpResponse implements Serializable {

    private String deviceId;
    private Integer port;
    private String host;
    private String type;
    private FileData fileData;
    private byte[] data;
    private String encodedData;

    public UdpResponse(String deviceId, Integer port, String host) {
        this.deviceId = deviceId;
        this.port = port;
        this.host = host;
    }

    public UdpResponse(byte[] data) {
        this.data = data;
    }

    public UdpResponse(String type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public UdpResponse(String type, String encodedData) {
        this.type = type;
        this.encodedData = encodedData;
    }
}
