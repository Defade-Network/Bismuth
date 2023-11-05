package net.defade.bismuth.core.packet.client.minestom;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.handlers.clientbound.ClientMinestomPacketHandler;

public record ClientMinestomVelocityForwardKeyPacket(byte[] forwardKey) implements Packet<ClientMinestomPacketHandler> {
    public ClientMinestomVelocityForwardKeyPacket(BismuthByteBuffer buffer) {
        this(buffer.getByteArray());
    }

    @Override
    public void write(BismuthByteBuffer buffer) {
        buffer.putByteArray(forwardKey);
    }

    @Override
    public void handle(ClientMinestomPacketHandler packetHandler) throws Throwable {
        packetHandler.handleVelocityForwardKey(this);
    }
}
