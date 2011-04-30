package org.sos.helix.client.websocket;

public class WebSocketClient {
    final WebSocketCallback callback;

    /**
     * @param callback - used for websocket callback
     */
    public WebSocketClient(WebSocketCallback callback) {
        this.callback = callback;
    }

    private final void onopen() {
        callback.connected();
    }

    private final void onclose() {
        callback.disconnected();
    }

    private final void onmessage(String message) {
        callback.message(message);
    }

    public native void connect(String server) /*-{
        var that = this;
        if (!$wnd.WebSocket) {
            alert("WebSocket connections not supported by this browser");
            return;
        }
        console.log("WebSocket connecting to "+server);
        that._ws=new $wnd.WebSocket(server);
        console.log("WebSocket connected "+that._ws.readyState);

        that._ws.onopen = function() {
            if(!that._ws) {
                console.log("WebSocket not really opened?");
                console.log("WebSocket["+server+"]._ws.onopen()");
                return;
            }
             console.log("onopen, readyState: "+that._ws.readyState);
             that.@org.sos.helix.client.websocket.WebSocketClient::onopen()();
             console.log("onopen done");
        };


        that._ws.onmessage = function(response) {
            if (response.data) {
                that.@org.sos.helix.client.websocket.WebSocketClient::onmessage(Ljava/lang/String;)( response.data );
            }
        };

        that._ws.onclose = function(m) {
             console.log("WebSocket["+server+"]_ws.onclose() state:"+that._ws.readyState);
             that.@org.sos.helix.client.websocket.WebSocketClient::onclose()();
        };
    }-*/;

    public native void send(String message) /*-{
        if (this._ws) {
            this._ws.send(message);
        } else {
            alert("not connected!" + this._ws);
        }
    }-*/;

    public native void close() /*-{
        console.log("WebSocket closing");
        this._ws.close();
        console.log("WebSocket closed");
    }-*/;

}