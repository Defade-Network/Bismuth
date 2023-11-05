package net.defade.bismuth.core.packet.server.login;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.BismuthProtocol;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.handlers.serverbound.ServerLoginPacketHandler;

public record ServerLoginRequestedProtocolPacket(BismuthProtocol requestedProtocol, BismuthByteBuffer clientInfos) implements Packet<ServerLoginPacketHandler> {
    public ServerLoginRequestedProtocolPacket(BismuthByteBuffer buffer) {
        this(BismuthProtocol.values()[buffer.get()], buffer);
    }

    @Override
    public void write(BismuthByteBuffer buffer) {
        buffer.put((byte) requestedProtocol.ordinal());
    }

    @Override
    public void handle(ServerLoginPacketHandler packetHandler) throws Throwable {
        packetHandler.handleRequestedProtocol(this);
    }
}
