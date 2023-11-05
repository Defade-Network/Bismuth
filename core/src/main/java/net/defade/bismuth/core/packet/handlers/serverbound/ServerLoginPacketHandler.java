package net.defade.bismuth.core.packet.handlers.serverbound;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.BismuthProtocol;
import net.defade.bismuth.core.Connection;
import net.defade.bismuth.core.packet.client.login.ClientLoginInfosPacket;
import net.defade.bismuth.core.packet.client.login.ClientLoginPasswordValidationPacket;
import net.defade.bismuth.core.packet.client.login.ClientLoginRSAKeyPacket;
import net.defade.bismuth.core.packet.handlers.PacketHandler;
import net.defade.bismuth.core.packet.server.login.ServerLoginAESKeyPacket;
import net.defade.bismuth.core.packet.server.login.ServerLoginPasswordPacket;
import net.defade.bismuth.core.packet.server.login.ServerLoginRequestedProtocolPacket;
import net.defade.bismuth.core.utils.CryptoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.function.Function;

public class ServerLoginPacketHandler implements PacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerLoginPacketHandler.class);
    private static KeyPair rsaKey;

    private final Function<BismuthProtocol, ServerPacketHandler> protocolToPacketHandler;
    private final Connection connection;
    private final byte[] hashedPassword;

    public ServerLoginPacketHandler(Function<BismuthProtocol, ServerPacketHandler> protocolToPacketHandler, Connection connection, byte[] hashedPassword) {
        this.protocolToPacketHandler = protocolToPacketHandler;
        this.connection = connection;
        this.hashedPassword = hashedPassword;

        if(rsaKey == null) {
            try {
                rsaKey = CryptoUtils.generateRSAKey();
            } catch (NoSuchAlgorithmException exception) {
                LOGGER.error("Error while generating RSA key", exception);
                connection.disconnect();
            }
        }
    }

    @Override
    public void onActivate() {
        connection.sendPacket(new ClientLoginRSAKeyPacket(rsaKey.getPublic()));
    }

    @Override
    public void onDisconnect() {

    }

    public void handleAESKey(ServerLoginAESKeyPacket serverLoginAESKeyPacket) throws NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] decryptedAESKey = CryptoUtils.decryptRSA(rsaKey.getPrivate(), serverLoginAESKeyPacket.encryptedAESKey());
        SecretKey aesKey = new SecretKeySpec(decryptedAESKey, "AES");

        connection.setupEncryption(aesKey);
    }

    public void handlePassword(ServerLoginPasswordPacket serverLoginPasswordPacket) throws NoSuchAlgorithmException {
        byte[] providedHashedPassword = CryptoUtils.hashSHA256(serverLoginPasswordPacket.password());

        if(Arrays.equals(providedHashedPassword, hashedPassword)) {
            connection.sendPacket(new ClientLoginPasswordValidationPacket(true));
        } else {
            connection.sendPacket(new ClientLoginPasswordValidationPacket(false));
            connection.disconnect();
        }
    }

    public void handleRequestedProtocol(ServerLoginRequestedProtocolPacket serverLoginRequestedProtocolPacket) {
        ServerPacketHandler packetHandler = protocolToPacketHandler.apply(serverLoginRequestedProtocolPacket.requestedProtocol());

        BismuthByteBuffer clientInfos = new BismuthByteBuffer(ByteBuffer.allocateDirect(4096)); // 4kb should be more than enough
        packetHandler.writeInfos(clientInfos);
        connection.sendPacket(new ClientLoginInfosPacket(clientInfos));

        packetHandler.setConnection(connection);
        connection.setPacketHandler(packetHandler);
    }
}
