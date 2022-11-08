package com.socket.processor;

import com.socket.api.Chunk;
import com.socket.api.FileData;
import com.socket.api.UdpResponse;
import com.socket.mapper.ObjectMapperWrapper;
import com.socket.model.domain.UdpSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.socket.common.Constant.MessageTypes.CHUNK;

@Slf4j
public class FileProcessor extends Processor {

    public FileProcessor(ObjectMapperWrapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean process(UdpResponse response, UdpSocketWrapper socket) {
        FileData fileData = response.getFileData();
        try {
            int offset = fileData.getOffset();
            String name = fileData.getName();
            if (socket.isValidChunk(name, offset)) {
                socket.getFilesChunks().put(name, offset);
                FileOutputStream fileOutputStream = new FileOutputStream(fileData.getName(), true);
                byte[] data = response.getData();
                fileOutputStream.write(data);
                fileOutputStream.close();
                sendReply(socket, name, offset);
            } else {
                log.error("Offset is invalid request offset {}, {}", offset, socket.getFilesChunks().get(name));
            }
        } catch (IOException e) {
            log.error("Unexpected error. Write file failed", e);
            return true;
        }
        return true;
    }

    private void sendReply(UdpSocketWrapper socketWrapper, String name, int offset) {
        UdpResponse response = createResponse(name, offset);
        socketWrapper.sendMessage(objectMapper.writeAsBytes(response));
    }

    private UdpResponse createResponse(String name, int offset) {
        UdpResponse response = new UdpResponse();
        Chunk chunk = new Chunk(name, offset);
        response.setType(CHUNK);
        response.setData(objectMapper.writeAsBytes(chunk));
        return response;
    }
}
