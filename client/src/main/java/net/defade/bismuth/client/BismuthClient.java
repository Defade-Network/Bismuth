package net.defade.bismuth.client;

import net.defade.bismuth.core.Connection;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.PacketFlow;
import net.defade.bismuth.core.packet.handlers.PacketHandler;
import net.defade.bismuth.core.packet.handlers.clientbound.ClientLoginPacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

abstract class BismuthClient extends PacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BismuthClient.class);
    private final String host;
    private final int port;
    private final byte[] password;

    private final Selector selector;

    private Connection connection;

    public BismuthClient(String host, int port, byte[] password) throws IOException {
        this.host = host;
        this.port = port;
        this.password = password;

        this.selector = Selector.open();
    }

    public final void connect() throws IOException {
        SocketChannel socket = SocketChannel.open(new InetSocketAddress(host, port));
        connection = new Connection(selector, socket, PacketFlow.SERVER_BOUND);
        connection.setPacketHandler(new ClientLoginPacketHandler(this, connection, password));

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
                            LOGGER.error("Error while reading channel", exception);
                            connection.disconnect();
                        }
                    });
                } catch (IOException exception) {
                    LOGGER.error("Error while selecting", exception);
                    connection.disconnect();
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