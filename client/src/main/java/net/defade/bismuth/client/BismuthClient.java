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
import java.util.concurrent.ExecutionException;

abstract class BismuthClient implements PacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BismuthClient.class);

    private final Selector selector;

    private Connection connection;

    public BismuthClient() throws IOException {
        this.selector = Selector.open();
    }

    public final void connect(String host, int port, byte[] password) throws IOException, ExecutionException, InterruptedException {
        SocketChannel socket = SocketChannel.open(new InetSocketAddress(host, port));
        connection = new Connection(selector, socket, PacketFlow.SERVER_BOUND);

        ClientLoginPacketHandler clientLoginPacketHandler = new ClientLoginPacketHandler(this, connection, password);
        connection.setPacketHandler(clientLoginPacketHandler);

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

        clientLoginPacketHandler.getConnectFuture().get();
    }

    public final void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    public final void sendPacket(Packet<?> packet) {
        if(connection == null) {
            throw new IllegalStateException("Cannot send a packet when not connected");
        }

        connection.sendPacket(packet);
    }
}