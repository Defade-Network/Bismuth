package net.defade.bismuth.server;

import net.defade.bismuth.core.Connection;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class BismuthServer {
    private final String host;
    private final int port;

    private final Selector selector;
    private final Map<SocketChannel, Connection> connectionMap = new HashMap<>();

    private boolean isRunning = false;

    public BismuthServer(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.selector = Selector.open();
    }

    public void bind() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        isRunning = true;

        Thread.ofVirtual().name("Bismuth Server Thread").start(() -> {
            while (isRunning) {
                try {
                    selector.select(selectionKey -> {
                        try {
                            if (selectionKey.isAcceptable()) {
                                SocketChannel socketChannel = serverSocketChannel.accept();
                                connectionMap.put(socketChannel, new Connection(socketChannel));

                                socketChannel.configureBlocking(false);
                                socketChannel.socket().setTcpNoDelay(true);

                                socketChannel.register(selector, SelectionKey.OP_READ);
                            }

                            if (selectionKey.isReadable()) {
                                connectionMap.get((SocketChannel) selectionKey.channel()).readChannel();
                            }
                        } catch (IOException exception) {
                            exception.printStackTrace();
                            // TODO log correctly
                        }
                    });
                } catch (IOException exception) {
                    exception.printStackTrace();
                    // TODO log correctly
                }
            }
        });
    }

    public void stop() {
        if (!isRunning) return;

        selector.wakeup();
        for (Connection connection : connectionMap.values()) {
            connection.disconnect();
        }

        isRunning = false;
    }
}