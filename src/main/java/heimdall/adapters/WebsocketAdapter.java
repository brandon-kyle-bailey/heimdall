package heimdall.adapters;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;

import heimdall.ports.LoggingPort;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;

public class WebsocketAdapter extends WebSocketServer {
  private final LoggingPort logManager;
  private final EventbusAdapter eventbus;
  private final ConcurrentHashMap<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocket>> channelSubscriptions = new ConcurrentHashMap<>();

  public WebsocketAdapter(LoggingPort logManager, EventbusAdapter eventbus, int port) {
    super(new InetSocketAddress(port));
    this.logManager = logManager;
    this.eventbus = eventbus;
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    this.logManager.debug("Connection opened: " + conn.getRemoteSocketAddress());
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    this.logManager.debug("Connection closed: " + conn.getRemoteSocketAddress());
    channelSubscriptions.forEach((channel, subscribers) -> unsubscribe(conn, channel));
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    this.logManager.debug("Received message: " + message);
    try {
      JSONObject json = new JSONObject(message);
      String action = json.optString("action");
      String requestId = json.optString("requestId");

      switch (action) {
        case "subscribe":
          String channel = json.getString("channel");
          subscribe(conn, channel);
          conn.send("Subscribed to channel: " + channel);
          break;
        case "mutation":
          String mutationChannel = json.getString("channel");
          String mutationPayload = json.getString("payload");
          this.eventbus.publish(mutationChannel, new JSONObject(mutationPayload));
          break;

        case "publish":
          String publishChannel = json.getString("channel");
          String payload = json.getString("payload");
          broadcast(publishChannel, payload);
          break;

        case "response":
          if (requestId != null) {
            handleResponse(requestId, json.optString("payload", ""));
          } else {
            this.logManager.error("Response received without a requestId.");
          }
          break;

        default:
          conn.send("Unrecognized action: " + action);
          break;
      }
    } catch (Exception e) {
      this.logManager.error("Error handling message: " + e.getMessage());
    }
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    this.logManager.error("Error: " + ex.getMessage());
  }

  @Override
  public void onStart() {
    this.logManager.debug("WebSocket server started on port " + getPort());
  }

  // Subscribe a connection to a channel
  private void subscribe(WebSocket conn, String channel) {
    channelSubscriptions.computeIfAbsent(channel, k -> new CopyOnWriteArraySet<>()).add(conn);
    this.logManager.debug(conn.getRemoteSocketAddress() + " subscribed to " + channel);
  }

  // Unsubscribe a connection from a channel
  private void unsubscribe(WebSocket conn, String channel) {
    CopyOnWriteArraySet<WebSocket> subscribers = channelSubscriptions.get(channel);
    if (subscribers != null) {
      subscribers.remove(conn);
      if (subscribers.isEmpty()) {
        channelSubscriptions.remove(channel);
      }
    }
    this.logManager.debug(conn.getRemoteSocketAddress() + " unsubscribed from " + channel);
  }

  // Broadcast a message to all subscribers of a channel
  private void broadcast(String channel, String message) {
    CopyOnWriteArraySet<WebSocket> subscribers = channelSubscriptions.get(channel);
    if (subscribers != null && !subscribers.isEmpty()) {
      for (WebSocket conn : subscribers) {
        try {
          conn.send(message);
        } catch (Exception e) {
          this.logManager.error("Failed to send message to " + conn.getRemoteSocketAddress());
        }
      }
    } else {
      this.logManager.debug("No subscribers for channel: " + channel);
    }
  }

  public CompletableFuture<String> broadcastToChannelSync(String channel, String requestPayload) {
    String requestId = UUID.randomUUID().toString();
    CompletableFuture<String> future = new CompletableFuture<>();
    pendingRequests.put(requestId, future);

    JSONObject request = new JSONObject();
    request.put("action", "request");
    request.put("requestId", requestId);
    request.put("payload", requestPayload);
    request.put("channel", channel);

    broadcast(channel, request.toString()); // Broadcast to the channel

    return future;
  }

  // Handle a response from a client
  private void handleResponse(String requestId, String payload) {
    CompletableFuture<String> future = pendingRequests.remove(requestId);
    if (future != null) {
      future.complete(payload);
      this.logManager.debug("Response received for requestId " + requestId + ": " + payload);
    } else {
      this.logManager.error("No pending request for requestId " + requestId);
    }
  }

  // Send a request and wait for a response
  public CompletableFuture<String> sendRequest(String channel, String payload) {
    String requestId = UUID.randomUUID().toString();
    CompletableFuture<String> future = new CompletableFuture<>();
    pendingRequests.put(requestId, future);

    JSONObject request = new JSONObject();
    request.put("action", "request");
    request.put("requestId", requestId);
    request.put("payload", payload);

    broadcast(channel, request.toString());
    return future;
  }
}
