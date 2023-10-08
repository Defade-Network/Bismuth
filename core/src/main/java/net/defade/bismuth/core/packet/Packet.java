package net.defade.bismuth.core.packet;

import net.defade.bismuth.core.packet.handlers.PacketHandler;

import java.nio.ByteBuffer;

public interface Packet<T extends PacketHandler> {
    void write(ByteBuffer buffer);

    void handle(T packetHandler);
}
