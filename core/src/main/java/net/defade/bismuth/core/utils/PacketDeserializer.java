package net.defade.bismuth.core.utils;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.handlers.PacketHandler;

public interface PacketDeserializer<T extends PacketHandler> {
    Packet<T> deserialize(BismuthByteBuffer buffer) throws Throwable;
}
