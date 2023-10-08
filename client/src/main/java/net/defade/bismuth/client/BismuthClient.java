package net.defade.bismuth.client;

import net.defade.bismuth.core.Connection;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.PacketFlow;
import net.defade.bismuth.core.packet.handlers.PacketHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

abstract class BismuthClient extends PacketHandler {
    private final String host;
    private final int port;

    private final Selector selector;

    private Connection connection;

    public BismuthClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.selector = Selector.open();
    }

    public final void connect() throws IOException {
        SocketChannel socket = SocketChannel.open(new InetSocketAddress(host, port));
        connection = new Connection(selector, socket, PacketFlow.SERVER_BOUND, this);

        socket.configureBlocking(false);
        socket.socket().setTcpNoDelay(true);

        socket.register(selector, SelectionKey.OP_READ);

        Thread.ofVirtual().name("Bismuth Client Thread").start(() -> {
            while (connection != null) {
                try {
                    selector.select(selectionKey -> {
                        try {
                            if (selectionKey.isReadable()) {
                                if(!connection.readChannel()) {
                                    connection = null;
                                }
                            }
                        } catch (IOException exception) {
                            exception.printStackTrace();
                            connection.disconnect();
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

    public final void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    public void sendPacket(Packet<?> packet) {
        if(connection == null) {
            throw new IllegalStateException("Cannot send a packet when not connected");
        }

        connection.sendPacket(packet);
    }
}