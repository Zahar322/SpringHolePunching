package com.socket.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class FileData implements Serializable {

    private String name;
    private String type;
    private int offset;
    private int chunkCount;

    public FileData(String name) {
        this.name = name;
    }
}
