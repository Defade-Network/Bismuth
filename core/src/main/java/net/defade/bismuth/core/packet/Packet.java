package net.defade.bismuth.core.packet;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.handlers.PacketHandler;

// The name of the packets should follow the following format: <PacketFlow><Protocol><PacketName>Packet
// Example: LoginClientRSAKeyPacket
public interface Packet<T extends PacketHandler> {
    void write(BismuthByteBuffer buffer);

    void handle(T packetHandler) throws Throwable;
}
