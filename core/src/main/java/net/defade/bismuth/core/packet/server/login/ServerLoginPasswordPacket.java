package net.defade.bismuth.core.packet.server.login;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.handlers.serverbound.ServerLoginPacketHandler;

public record ServerLoginPasswordPacket(byte[] password) implements Packet<ServerLoginPacketHandler> {
    public ServerLoginPasswordPacket(BismuthByteBuffer buffer) {
        this(buffer.getByteArray());
    }

    @Override
    public void write(BismuthByteBuffer buffer) {
        buffer.putByteArray(password);
    }

    @Override
    public void handle(ServerLoginPacketHandler packetHandler) throws Throwable {
        packetHandler.handlePassword(this);
    }
}
