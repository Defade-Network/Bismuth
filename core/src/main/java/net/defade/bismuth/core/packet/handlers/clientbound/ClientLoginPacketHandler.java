package net.defade.bismuth.core.packet.handlers.clientbound;

import net.defade.bismuth.core.BismuthProtocol;
import net.defade.bismuth.core.Connection;
import net.defade.bismuth.core.exceptions.DisconnectException;
import net.defade.bismuth.core.exceptions.InvalidPasswordException;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

public class ClientLoginPacketHandler extends PacketHandler {
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
            connection.sendPacket(new ServerLoginRequestedProtocolPacket(BismuthProtocol.getHandledProtocolByHandler(clientPacketHandler)));
            connectFuture.complete(null);
            connection.setPacketHandler(clientPacketHandler);
        }
    }

    public CompletableFuture<Void> getConnectFuture() {
        return connectFuture;
    }
}
