package com.socket.processor;

import com.socket.api.Chunk;
import com.socket.api.UdpResponse;
import com.socket.mapper.ObjectMapperWrapper;
import com.socket.model.domain.UdpSocketWrapper;

public class ChunkProcessor extends Processor {

    public ChunkProcessor(ObjectMapperWrapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean process(UdpResponse response, UdpSocketWrapper socket) {
        Chunk chunk = objectMapper.readBytes(response.getData(), Chunk.class);
        socket.getFilesChunks().put(chunk.getFilename(), chunk.getOffset());
        return true;
    }
}
