package org.sos.helix.client.websocket;

public interface WebSocketCallback {
    void connected();
    void disconnected();
    void message(String message);
}
