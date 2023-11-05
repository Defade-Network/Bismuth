package net.defade.bismuth.core.packet.handlers;

import net.defade.bismuth.core.BismuthByteBuffer;

public interface PacketHandler {
    /**
     * Called when the connection is established and ready
     */
    void onActivate();

    /**
     * Called when the connection is closed.
     */
    void onDisconnect();

    /**
     * Writes infos of the client/server like the Minestom server name for the server or the database credentials
     * for the Minestom client.
     * @param buffer The buffer to write the infos to
     */
    default void writeInfos(BismuthByteBuffer buffer) {

    }

    /**
     * Handles infos received by the client or server.
     * @param buffer The buffer to read the infos from
     */
    default void handleInfos(BismuthByteBuffer buffer) {

    }
}
