package net.defade.bismuth.client;

import net.defade.bismuth.core.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class BismuthClient {
    private final String host;
    private final int port;

    private final Selector selector;

    private Connection connection;

    public BismuthClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.selector = Selector.open();
    }

    public void connect() throws IOException {
        SocketChannel socket = SocketChannel.open(new InetSocketAddress(host, port));
        connection = new Connection(socket);

        socket.configureBlocking(false);
        socket.socket().setTcpNoDelay(true);

        socket.register(selector, SelectionKey.OP_READ);
    }

    public void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }
}