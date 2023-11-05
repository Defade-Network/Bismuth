package net.defade.bismuth.core.packet.client.login;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.handlers.clientbound.ClientLoginPacketHandler;

public record ClientLoginInfosPacket(BismuthByteBuffer infos) implements Packet<ClientLoginPacketHandler> {
    @Override
    public void write(BismuthByteBuffer buffer) {
        buffer.put(infos.getBackingByteBuffer());
    }

    @Override
    public void handle(ClientLoginPacketHandler packetHandler) throws Throwable {
        packetHandler.handleInfos(this);
    }
}
