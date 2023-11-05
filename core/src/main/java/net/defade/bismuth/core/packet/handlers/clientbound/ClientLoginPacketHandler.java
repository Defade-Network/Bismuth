package net.defade.bismuth.core.packet.handlers.clientbound;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.BismuthProtocol;
import net.defade.bismuth.core.Connection;
import net.defade.bismuth.core.exceptions.DisconnectException;
import net.defade.bismuth.core.exceptions.InvalidPasswordException;
import net.defade.bismuth.core.packet.client.login.ClientLoginInfosPacket;
import net.defade.bismuth.core.packet.client.login.ClientLoginPasswordValidationPacket;
import net.defade.bismuth.core.packet.client.login.ClientLoginRSAKeyPacket;
import net.defade.bismuth.core.packet.handlers.PacketHandler;
import net.defade.bismuth.core.packet.server.login.ServerLoginAESKeyPacket;
import net.defade.bismuth.core.packet.server.login.ServerLoginPasswordPacket;
import net.defade.bismuth.core.packet.server.login.ServerLoginRequestedProtocolPacket;
import net.defade.bismuth.core.utils.CryptoUtils;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

public class ClientLoginPacketHandler implements PacketHandler {
    private final CompletableFuture<Void> connectFuture = new CompletableFuture<>();

    private final PacketHandler clientPacketHandler;
    private final Connection connection;
    private final byte[] password;

    public ClientLoginPacketHandler(PacketHandler clientPacketHandler, Connection connection, byte[] password) {
        this.connection = connection;
        this.password = password;
        this.clientPacketHandler = clientPacketHandler;
    }

    @Override
    public void onActivate() {
        // We do nothing. The server is the first one to send a packet.
    }

    @Override
    public void onDisconnect() {
        connectFuture.completeExceptionally(new DisconnectException());
    }

    public void handleRSAKey(ClientLoginRSAKeyPacket clientLoginRSAKeyPacket) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey aesKey = CryptoUtils.generateAESKey();

        byte[] encryptedAESKey = CryptoUtils.encryptRSA(clientLoginRSAKeyPacket.rsaKey(), aesKey.getEncoded());
        connection.sendPacket(new ServerLoginAESKeyPacket(encryptedAESKey));

        connection.setupEncryption(aesKey);

        connection.sendPacket(new ServerLoginPasswordPacket(password));
    }

    public void handlePasswordValidation(ClientLoginPasswordValidationPacket clientLoginPasswordValidationPacket) throws InvalidPasswordException {
        if(!clientLoginPasswordValidationPacket.isPasswordValid()) {
            connection.disconnect();
            throw new InvalidPasswordException();
        } else {
            BismuthByteBuffer clientInfos = new BismuthByteBuffer(ByteBuffer.allocateDirect(4096)); // 4kb should be more than enough
            clientPacketHandler.writeInfos(clientInfos);

            connection.sendPacket(new ServerLoginRequestedProtocolPacket(BismuthProtocol.getHandledProtocolByHandler(clientPacketHandler), clientInfos));
            connectFuture.complete(null);
        }
    }

    public void handleInfos(ClientLoginInfosPacket clientLoginInfosPacket) {
        clientPacketHandler.handleInfos(clientLoginInfosPacket.infos());
        connection.setPacketHandler(clientPacketHandler);
    }

    public CompletableFuture<Void> getConnectFuture() {
        return connectFuture;
    }
}
