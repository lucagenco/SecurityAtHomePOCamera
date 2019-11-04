package com.test.testmotioncapture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import okhttp3.Response;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketListener extends okhttp3.WebSocketListener {
    @Override
    public void onClosed(@NotNull okhttp3.WebSocket webSocket, int code, @NotNull String reason) {
        webSocket.send("json a stringyfier");
        webSocket.close(1000, "Goodbye");
    }

    @Override
    public void onClosing(@NotNull okhttp3.WebSocket webSocket, int code, @NotNull String reason) {
        webSocket.close(1000, null);
    }

    @Override
    public void onFailure(@NotNull okhttp3.WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
    }

    @Override
    public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
    }

    @Override
    public void onOpen(@NotNull okhttp3.WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
    }
}
