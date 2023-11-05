package net.defade.bismuth.client;

import net.defade.bismuth.core.BismuthByteBuffer;
import net.defade.bismuth.core.packet.handlers.clientbound.ClientMinestomPacketHandler;
import java.io.IOException;

public abstract class MinestomClient extends BismuthClient implements ClientMinestomPacketHandler {
    private final String serverName;

    public MinestomClient(String serverName) throws IOException {
        this.serverName = serverName;
    }

    @Override
    public final void writeInfos(BismuthByteBuffer buffer) {
        buffer.putString(serverName);
    }

    @Override
    public final void handleInfos(BismuthByteBuffer buffer) {

    }
}
