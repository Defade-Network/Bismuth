package net.defade.bismuth.core.packet.handlers.serverbound;

import net.defade.bismuth.core.Connection;
import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.handlers.PacketHandler;

public abstract class ServerPacketHandler implements PacketHandler {
    private Connection connection;

    public final void disconnect() {
        connection.disconnect();
    }

    public final void sendPacket(Packet<?> packet) {
        connection.sendPacket(packet);
    }

    public final void setConnection(Connection connection) {
        this.connection = connection;
    }
}
