package com.socket.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocketData implements Serializable {

    private Integer internalPort;
    private Integer port;
    private String host;
    private List<String> fileNames;

    public List<String> getFileNames() {
        if (fileNames == null) {
            fileNames = new ArrayList<>();
        }
        return fileNames;
    }
}
