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

    private final PacketFlow packetFlow; // Flow in which the packets are sent
    private BismuthProtocol protocol = BismuthProtocol.LOGIN;
    private PacketHandler loginPacketHandler; // Only used during the login stage
    private PacketHandler packetHandler;

    // TODO: add javadoc
    public Connection(Selector selector, SocketChannel channel, PacketFlow packetFlow, PacketHandler packetHandler) {
        this.selector = selector;
        this.channel = channel;
        this.packetFlow = packetFlow;
        this.packetHandler = packetHandler;

        loginPacketHandler = switch (packetFlow) {
            case CLIENT_BOUND -> new ServerLoginPacketHandler();
            case SERVER_BOUND -> new ClientLoginPacketHandler();
        };
    }

    /**
     * Reads from the channel and handle the packets
     * @return Whether the connection is still up
     * @throws IOException If an I/O error occurs
     */
    public boolean readChannel() throws IOException {
        int readBytes = channel.read(readBuffer);
        if (readBytes == -1) {
            disconnect();
            return false;
        }

        while (readBuffer.position() > 2) {
            readBuffer.flip();
            short packetSize = readBuffer.getShort();
            if (packetSize < 0) {
                // TODO implement disconnect message
                disconnect();
                return false;
            }

            if (readBuffer.remaining() >= packetSize) {
                ByteBuffer packetBuffer = readBuffer.slice(2, packetSize);
                Packet<?> packet = protocol.createPacket(packetFlow.opposite(), packetBuffer.get(), packetBuffer);

                if(protocol == BismuthProtocol.LOGIN) {
                    handlePacketGenericHack(packet, loginPacketHandler);
                } else {
                    handlePacketGenericHack(packet, packetHandler);
                }

                readBuffer.position(packetSize);
                readBuffer.compact();
            } else {
                readBuffer.position(readBuffer.limit());
                break;
            }
            readBuffer.flip();
        }

        readBuffer.clear();

        return true;
    }

    public void sendPacket(Packet<?> packet) {
        // TODO: use a pool of buffers
        ByteBuffer packetBuffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
        packetBuffer.putShort((short) 0); // Packet size, it will be overwritten later as we don't know the size yet

        Integer packetId = protocol.getPacketId(packetFlow, packet);
        if(packetId == null) {
            throw new IllegalArgumentException("Packet " + packet.getClass().getSimpleName() + " is not registered for protocol " + protocol.name());
        }
        packetBuffer.put(packetId.byteValue());

        packet.write(packetBuffer);
        // Overwrite the packet size
        packetBuffer.putShort(0, (short) (packetBuffer.position() - 2)); // We don't include the two bytes for
                                                                               // the packet size in the packet size
        for (int i = 0; i < packetBuffer.position(); i++) {
            System.out.print(packetBuffer.get(i) + " ");
        }
        packetBuffer.flip();

        try {
            channel.write(packetBuffer);
        } catch (IOException exception) {
            exception.printStackTrace();
            // TODO log correctly
        }
    }

    public void disconnect() {
        if(packetHandler != null) packetHandler.onDisconnect();
        channel.keyFor(selector).cancel();

        try {
            channel.close();
        } catch (IOException ignored) { }
    }

    public boolean isConnected() {
        return channel.isConnected();
    }

    /**
     * Hack around Java's inability to support generic type inference.
     * @param packet The packet to handle
     * @param packetHandler The packet handler to handle the packet
     * @param <T> The type of the packet handler
     */
    @SuppressWarnings("unchecked")
    private static <T extends PacketHandler> void handlePacketGenericHack(Packet<T> packet, PacketHandler packetHandler) {
        packet.handle((T) packetHandler);
    }
}
