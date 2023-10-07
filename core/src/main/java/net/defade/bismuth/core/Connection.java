package net.defade.bismuth.core;

import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.PacketFlow;
import net.defade.bismuth.core.packet.handlers.PacketHandler;
import net.defade.bismuth.core.packet.handlers.clientbound.ClientLoginPacketHandler;
import net.defade.bismuth.core.packet.handlers.serverbound.ServerLoginPacketHandler;
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

    private final PacketFlow packetFlow;
    private BismuthProtocol protocol = BismuthProtocol.LOGIN;
    private PacketHandler packetHandler;
    private PacketHandler loginPacketHandler; // Only used during the login stage

    public Connection(Selector selector, SocketChannel channel, PacketFlow packetFlow, PacketHandler packetHandler) {
        this.selector = selector;
        this.channel = channel;
        this.packetFlow = packetFlow;
        this.packetHandler = packetHandler;

        loginPacketHandler = switch (packetFlow) {
            case CLIENT_BOUND -> new ClientLoginPacketHandler();
            case SERVER_BOUND -> new ServerLoginPacketHandler();
        };
    }

    public void readChannel() throws IOException {
        int readBytes = channel.read(readBuffer);
        if (readBytes == -1) {
            disconnect();
            return;
        }

        while (readBuffer.position() > 2) {
            readBuffer.flip();
            short packetSize = readBuffer.getShort();
            if (packetSize < 0) {
                // TODO implement disconnect message
                disconnect();
                return;
            }

            if (packetSize <= readBuffer.remaining()) {
                ByteBuffer packetBuffer = readBuffer.slice(2, packetSize);
                // TODO: read packet
                readBuffer.position(packetSize); // TODO: is the packet size short included?
                readBuffer.compact();
            }
            readBuffer.flip();
        }

        readBuffer.clear();
    }

    public void sendPacket(Packet<?> packet) {
        // TODO: implement
    }

    public void disconnect() {
        packetHandler.onDisconnect();
        channel.keyFor(selector).cancel();

        try {
            channel.close();
        } catch (IOException ignored) { }
    }

    public boolean isConnected() {
        return channel.isConnected();
    }
}
