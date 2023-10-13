package net.defade.bismuth.core.exceptions;

import java.io.IOException;

public class NegativePacketSizeException extends IOException {
    public NegativePacketSizeException() {
        super("Packet size cannot be negative");
    }
}
