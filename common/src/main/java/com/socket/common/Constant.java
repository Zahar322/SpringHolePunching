package com.socket.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constant {

    @UtilityClass
    public static final class Commands {

        public static final String CONNECT = "connect";
        public static final String ANNOUNCE = "announce";
        public static final String KEEP_ALIVE = "keep-alive";
        public static final String WAITING = "waiting";
        public static final String SEND = "send";
        public static final String P2P = "P2P";
    }

    @UtilityClass
    public static final class MessageTypes {

        public static final String TARGET = "target";
        public static final String STRING = "string";
        public static final String SOUND = "sound";
        public static final String FILE = "file";
        public static final String CHUNK = "chunk";
    }
}
