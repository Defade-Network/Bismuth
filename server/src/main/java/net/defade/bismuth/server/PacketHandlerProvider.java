package net.defade.bismuth.server;

import net.defade.bismuth.core.packet.handlers.serverbound.ServerPacketHandler;

public interface PacketHandlerProvider {
    ServerPacketHandler getMinestomPacketHandler();
}
