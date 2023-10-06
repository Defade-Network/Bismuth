package net.defade.bismuth.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * This class represents a server or a client connection and handles buffering, encryption, etc.
 */
public class Connection {
    private static final int MAX_PACKET_SIZE = Short.MAX_VALUE;

    private final SocketChannel channel;
    private final ByteBuffer readBuffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);

    public Connection(SocketChannel channel) {
        this.channel = channel;
    }

    public void readChannel() throws IOException {
        channel.read(readBuffer);
    }

    public void disconnect() {
        try {
            channel.close();
        } catch (IOException ignored) { }
    }
}
