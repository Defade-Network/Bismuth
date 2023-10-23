package net.defade.bismuth.core.packet.handlers;

public interface PacketHandler {
    /**
     * Called when the connection is established and ready
     */
    void onActivate();

    /**
     * Called when the connection is closed.
     */
    void onDisconnect();
}
