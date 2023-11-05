package net.defade.bismuth.core.packet.handlers.serverbound;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.server.minestom.ServerMinestomAllowPlayersPacket;

public abstract class ServerMinestomPacketHandler extends ServerPacketHandler {
    private String serverName;

    public abstract void handleAllowPlayers(ServerMinestomAllowPlayersPacket serverMinestomAllowPlayersPacket);

    @Override
    public final void writeInfos(BismuthByteBuffer buffer) {

    }

    @Override
    public final void handleInfos(BismuthByteBuffer buffer) {
        serverName = buffer.getString();
    }

    public String getServerName() {
        return serverName;
    }
}
