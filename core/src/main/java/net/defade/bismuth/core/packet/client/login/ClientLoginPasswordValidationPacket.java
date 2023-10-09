package net.defade.bismuth.core.packet.client.login;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.handlers.clientbound.ClientLoginPacketHandler;

public record ClientLoginPasswordValidationPacket(boolean isPasswordValid) implements Packet<ClientLoginPacketHandler> {
    public ClientLoginPasswordValidationPacket(BismuthByteBuffer buffer) {
        this(buffer.getBoolean());
    }
    @Override
    public void write(BismuthByteBuffer buffer) {
        buffer.putBoolean(isPasswordValid);
    }

    @Override
    public void handle(ClientLoginPacketHandler packetHandler) throws Throwable {
        packetHandler.handlePasswordValidation(this);
    }
}
