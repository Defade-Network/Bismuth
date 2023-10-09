package net.defade.bismuth.core.packet.client.login;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.handlers.clientbound.ClientLoginPacketHandler;
import net.defade.bismuth.core.utils.CryptoUtils;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public record ClientLoginRSAKeyPacket(PublicKey rsaKey) implements Packet<ClientLoginPacketHandler> {
    public ClientLoginRSAKeyPacket(BismuthByteBuffer buffer) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this(CryptoUtils.getRSAKeyFromBytes(buffer.getByteArray()));
    }

    @Override
    public void write(BismuthByteBuffer buffer) {
        buffer.putByteArray(rsaKey.getEncoded());
    }

    @Override
    public void handle(ClientLoginPacketHandler packetHandler) throws Throwable {
        packetHandler.handleRSAKey(this);
    }
}
