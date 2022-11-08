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
public class SocketRequest implements Serializable {

    private SocketData data;
    private List<Target> targets;

    public List<Target> getTargets() {
        if (targets == null) {
            targets = new ArrayList<>();
        }
        return targets;
    }
}
