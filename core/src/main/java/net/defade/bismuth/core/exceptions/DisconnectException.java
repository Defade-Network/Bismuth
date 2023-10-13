package net.defade.bismuth.core.exceptions;

import java.io.IOException;

public class DisconnectException extends IOException {
    public DisconnectException() {
        super("Disconnected");
    }
}
