package net.defade.bismuth.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * This class represents a server or a client connection and handles buffering, encryption, etc.
 */
public class Connection {
    private static final int MAX_PACKET_SIZE = Short.MAX_VALUE;

    private final Selector selector;
    private final SocketChannel channel;
    private final ByteBuffer readBuffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);

    public Connection(Selector selector, SocketChannel channel) {
        this.selector = selector;
        this.channel = channel;
    }

    public void readChannel() throws IOException {
        int readBytes = channel.read(readBuffer);
        if (readBytes == -1) {
            disconnect();
            return;
        }

        readBuffer.clear();
    }

    public void disconnect() {
        channel.keyFor(selector).cancel();

        try {
            channel.close();
        } catch (IOException ignored) { }
    }

    public boolean isConnected() {
        return channel.isConnected();
    }
}
