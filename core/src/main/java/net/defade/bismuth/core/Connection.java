package net.defade.bismuth.core;

import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.PacketFlow;
import net.defade.bismuth.core.packet.handlers.PacketHandler;
import net.defade.bismuth.core.utils.CryptoUtils;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * This class represents a server or a client connection and handles buffering, encryption, etc.
 */
public class Connection {
    private static final int MAX_PACKET_SIZE = Short.MAX_VALUE;

    private final Selector selector;
    private final SocketChannel channel;

    private final ByteBuffer readBuffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
    private EncryptionContainer encryptionContainer;

    private final PacketFlow packetFlow; // Flow in which the packets are sent
    private BismuthProtocol protocol;
    private PacketHandler packetHandler;

    // TODO: add javadoc
    public Connection(Selector selector, SocketChannel channel, PacketFlow packetFlow) {
        this.selector = selector;
        this.channel = channel;
        this.packetFlow = packetFlow;
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

                if (encryptionContainer != null) { // if encryption is enabled, decrypt the packet
                    try {
                        ByteBuffer bufferToDecrypt = packetBuffer.slice();
                        encryptionContainer.decrypt.update(
                                bufferToDecrypt,
                                packetBuffer.duplicate()
                        );
                    } catch (ShortBufferException exception) {
                        exception.printStackTrace(); // TODO log correctly
                        disconnect();
                    }
                }

                Packet<?> packet = null;
                try {
                    packet = protocol.createPacket(packetFlow.opposite(), packetBuffer.get(), new BismuthByteBuffer(packetBuffer));
                } catch (Throwable throwable) {
                    throwable.printStackTrace(); // TODO log correctly
                    disconnect();
                }

                // Handle the packet
                try {
                    handlePacketGenericHack(packet, packetHandler);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    // TODO log correctly
                    disconnect();
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

        packet.write(new BismuthByteBuffer(packetBuffer));
        // Overwrite the packet size
        packetBuffer.putShort(0, (short) (packetBuffer.position() - 2)); // We don't include the two bytes for
                                                                               // the packet size in the packet size

        // if encryption is enabled, encrypt the packet
        if(encryptionContainer != null) {
            try {
                ByteBuffer bufferToEncrypt = packetBuffer.slice(2, packetBuffer.position() - 2);
                encryptionContainer.encrypt.update(
                        bufferToEncrypt,
                        packetBuffer.duplicate()
                );
            } catch (ShortBufferException exception) {
                exception.printStackTrace(); // TODO log correctly
                disconnect();
            }
        }

        packetBuffer.flip();

        try {
            channel.write(packetBuffer);
        } catch (IOException exception) {
            exception.printStackTrace();
            // TODO log correctly
        }
    }

    /**
     * Sets the packet handler and the protocol treated by the handler
     * @param packetHandler The packet handler
     */
    public void setPacketHandler(PacketHandler packetHandler) {
        this.protocol = BismuthProtocol.getHandledProtocolByHandler(packetHandler);
        this.packetHandler = packetHandler;
        this.packetHandler.onActivate();
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

    public void setupEncryption(SecretKey aesKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        this.encryptionContainer = new EncryptionContainer(CryptoUtils.createAESEncryptCipher(aesKey), CryptoUtils.createAESDecryptCipher(aesKey));
    }

    /**
     * Hack around Java's inability to support generic type inference.
     * @param packet The packet to handle
     * @param packetHandler The packet handler to handle the packet
     * @param <T> The type of the packet handler
     */
    @SuppressWarnings("unchecked")
    private static <T extends PacketHandler> void handlePacketGenericHack(Packet<T> packet, PacketHandler packetHandler) throws Throwable {
        packet.handle((T) packetHandler);
    }

    private record EncryptionContainer(Cipher encrypt, Cipher decrypt) { }
}
