package net.defade.bismuth.core.packet.server.login;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.handlers.serverbound.ServerLoginPacketHandler;

public record ServerLoginAESKeyPacket(byte[] encryptedAESKey) implements Packet<ServerLoginPacketHandler> {
    public ServerLoginAESKeyPacket(BismuthByteBuffer buffer) {
        this(buffer.getByteArray());
    }

    @Override
    public void write(BismuthByteBuffer buffer) {
        buffer.putByteArray(encryptedAESKey);
    }

    @Override
    public void handle(ServerLoginPacketHandler packetHandler) throws Throwable {
        packetHandler.handleAESKey(this);
    }
}
