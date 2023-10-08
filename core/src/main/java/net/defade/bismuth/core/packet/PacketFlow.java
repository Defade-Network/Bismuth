package net.defade.bismuth.core.packet;

public enum PacketFlow {
    SERVER_BOUND, // client → server
    CLIENT_BOUND;  // server → client

    public PacketFlow opposite() {
        return switch (this) {
            case SERVER_BOUND -> CLIENT_BOUND;
            case CLIENT_BOUND -> SERVER_BOUND;
        };
    }
}
