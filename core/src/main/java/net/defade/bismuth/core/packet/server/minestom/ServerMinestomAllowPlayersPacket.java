package net.defade.bismuth.core.packet.server.minestom;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.handlers.serverbound.ServerMinestomPacketHandler;

public record ServerMinestomAllowPlayersPacket(boolean allowPlayers) implements Packet<ServerMinestomPacketHandler> {
    public ServerMinestomAllowPlayersPacket(BismuthByteBuffer buffer) {
        this(buffer.getBoolean());
    }

    @Override
    public void write(BismuthByteBuffer buffer) {
        buffer.putBoolean(allowPlayers);
    }

    @Override
    public void handle(ServerMinestomPacketHandler packetHandler) throws Throwable {
        packetHandler.handleAllowPlayers(this);
    }
}
