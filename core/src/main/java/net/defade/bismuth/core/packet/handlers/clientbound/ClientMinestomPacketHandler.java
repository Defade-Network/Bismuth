package net.defade.bismuth.core.packet.handlers.clientbound;

import net.defade.bismuth.core.packet.client.minestom.ClientMinestomVelocityForwardKeyPacket;
import net.defade.bismuth.core.packet.handlers.PacketHandler;

public interface ClientMinestomPacketHandler extends PacketHandler {
    void handleVelocityForwardKey(ClientMinestomVelocityForwardKeyPacket clientMinestomVelocityForwardKeyPacket);
}
