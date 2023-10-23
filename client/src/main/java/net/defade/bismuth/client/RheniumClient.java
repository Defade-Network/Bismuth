package net.defade.bismuth.client;

import net.defade.bismuth.core.packet.handlers.clientbound.ClientRheniumPacketHandler;
import java.io.IOException;

public abstract class RheniumClient extends BismuthClient implements ClientRheniumPacketHandler {
    public RheniumClient(String host, int port, byte[] password) throws IOException {
        super(host, port, password);
    }
}
