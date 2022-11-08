package com.socket.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.DatagramPacket;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Target {

    private String host;
    private Integer port;

    public Target(DatagramPacket packet) {
        this(packet.getAddress().getHostName(), packet.getPort());
    }
}
