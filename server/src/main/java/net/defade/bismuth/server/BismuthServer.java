package net.defade.bismuth.server;

import net.defade.bismuth.core.BismuthProtocol;
import net.defade.bismuth.core.Connection;
import net.defade.bismuth.core.packet.PacketFlow;
import net.defade.bismuth.core.packet.handlers.PacketHandler;
import net.defade.bismuth.core.packet.handlers.serverbound.ServerLoginPacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BismuthServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BismuthServer.class);

    private final String host;
    private final int port;
    private final byte[] hashedPassword;
    private final PacketHandlerProvider packetHandlerProvider;

    private final Selector selector;
    private final Map<SocketChannel, Connection> connectionMap = new HashMap<>();

    private boolean isRunning = false;

    public BismuthServer(String host, int port, byte[] hashedPassword, PacketHandlerProvider packetHandlerProvider) throws IOException {
        this.host = host;
        this.port = port;
        this.hashedPassword = hashedPassword;
        this.packetHandlerProvider = packetHandlerProvider;

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

                                Connection connection = new Connection(selector, socketChannel, PacketFlow.CLIENT_BOUND);
                                connection.setPacketHandler(new ServerLoginPacketHandler(packetHandlerProviderToFunction(), connection, hashedPassword));
                                connectionMap.put(socketChannel, connection);

                                socketChannel.configureBlocking(false);
                                socketChannel.socket().setTcpNoDelay(true);

                                socketChannel.register(selector, SelectionKey.OP_READ);
                            }

                            if (selectionKey.isReadable()) {
                                Connection connection = connectionMap.get((SocketChannel) selectionKey.channel());
                                if(!connection.readChannel()) {
                                    connectionMap.remove((SocketChannel) selectionKey.channel());
                                }
                            }
                        } catch (IOException exception) {
                            LOGGER.error("Error while reading channel", exception);

                            Connection connection = connectionMap.get((SocketChannel) selectionKey.channel());
                            if (connection != null) {
                                connection.disconnect();
                            }
                        }
                    });
                } catch (IOException exception) {
                    LOGGER.error("Error while selecting", exception);
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

    /**
     * Method to get around directly asking for the function,
     * so users don't have to handle the LOGIN protocol or forget a protocol.
     * @return A function to get a packet handler for the protocol
     */
    private Function<BismuthProtocol, PacketHandler> packetHandlerProviderToFunction() {
        return protocol -> switch (protocol) {
            case YOKURA -> packetHandlerProvider.getYokuraPacketHandler();

            default -> {
                throw new IllegalStateException("Unexpected value: " + protocol);
            }
        };
    }
}