package heimdall.infrastructure.adapters.websockets;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;

import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;

import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public class WebsocketAdapter extends WebSocketServer {
  private final EventbusAdapter eventbus;
  private final LoggingPort logManager;
  private final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocket>> channelSubscriptions = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Consumer<String>> defaultSubscribers = new ConcurrentHashMap<>();

  public WebsocketAdapter(LoggingPort logManager, int port, EventbusAdapter eventbus, String[] channels) {
    super(new InetSocketAddress(port));
    this.eventbus = eventbus;
    this.logManager = logManager;

    // Initialize predefined channels
    for (String channel : channels) {
      channelSubscriptions.putIfAbsent(channel, new CopyOnWriteArraySet<>());
      this.logManager.debug("Initialized channel: %s".formatted(channel));
    }
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    this.logManager.debug("WebSocket connection opened: %s".formatted(conn.getRemoteSocketAddress()));
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    this.logManager.debug("WebSocket connection closed: %s".formatted(conn.getRemoteSocketAddress()));
    channelSubscriptions.forEach((channel, subscribers) -> unsubscribeFromChannel(conn, channel));
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    this.logManager.debug("Received WebSocket message: %s".formatted(message));
    try {
      JSONObject jsonEvent = new JSONObject(message);
      String action = jsonEvent.optString("action");

      switch (action) {
        case "subscribe":
          String subscribeChannel = jsonEvent.getString("channel");
          subscribeToChannel(conn, subscribeChannel);
          conn.send("Subscribed to channel: %s".formatted(subscribeChannel));
          break;

        case "unsubscribe":
          String unsubscribeChannel = jsonEvent.getString("channel");
          unsubscribeFromChannel(conn, unsubscribeChannel);
          conn.send("Unsubscribed from channel: %s".formatted(unsubscribeChannel));
          break;

        case "publish":
          String publishChannel = jsonEvent.getString("channel");
          String payload = jsonEvent.getString("payload");
          // Publish to eventbus
          this.eventbus.publish(publishChannel, new JSONObject(payload));
          // Broadcast to channel
          String response = broadcastToChannelSync(publishChannel, payload);
          conn.send("Broadcast completed with response: %s".formatted(response));
          break;
        case "response":
          String responseChannel = jsonEvent.getString("channel");
          String responsePayload = jsonEvent.getString("payload");
          // Broadcast to channel
          String result = broadcastToChannelSync(responseChannel, responsePayload);
          conn.send("Broadcast completed with response: %s".formatted(result));
          break;

        default:
          this.logManager.warn("Unrecognized action: %s".formatted(action));
          conn.send("Error: Unrecognized action");
          break;
      }
    } catch (Exception e) {
      this.logManager.error("Failed to handle message: %s".formatted(message));
      conn.send("Error: Invalid message format");
    }
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    this.logManager.error("WebSocket error: %s".formatted(ex.getMessage()));
    ex.printStackTrace();
  }

  @Override
  public void onStart() {
    this.logManager.debug("WebSocket server started.");
  }

  // Subscribe a connection to a channel
  private void subscribeToChannel(WebSocket conn, String channel) {
    channelSubscriptions.computeIfAbsent(channel, k -> new CopyOnWriteArraySet<>()).add(conn);
    this.logManager.debug("Connection %s subscribed to channel %s".formatted(conn.getRemoteSocketAddress(), channel));
  }

  // Unsubscribe a connection from a channel
  private void unsubscribeFromChannel(WebSocket conn, String channel) {
    CopyOnWriteArraySet<WebSocket> subscribers = channelSubscriptions.get(channel);
    if (subscribers != null) {
      subscribers.remove(conn);
      if (subscribers.isEmpty()) {
        channelSubscriptions.remove(channel);
      }
    }
    this.logManager
        .debug("Connection %s unsubscribed from channel %s".formatted(conn.getRemoteSocketAddress(), channel));
  }

  // Synchronous broadcast to a channel and collect responses
  @SuppressWarnings("unchecked")
  public String broadcastToChannelSync(String channel, String message) {
    CopyOnWriteArraySet<WebSocket> subscribers = channelSubscriptions.get(channel);
    if (subscribers == null || subscribers.isEmpty()) {
      // Use default subscriber if available
      Consumer<String> defaultSubscriber = defaultSubscribers.get(channel);
      if (defaultSubscriber != null) {
        defaultSubscriber.accept(message);
        return "Handled by default subscriber for channel %s".formatted(channel);
      }
      return "No subscribers";
    }

    // Use CompletableFuture to wait for all responses
    CompletableFuture<String>[] futures = subscribers.stream()
        .map(conn -> CompletableFuture.supplyAsync(() -> {
          try {
            conn.send(message);
            return "Response from %s".formatted(conn.getRemoteSocketAddress());
          } catch (Exception e) {
            this.logManager.error("Failed to send message to %s".formatted(conn.getRemoteSocketAddress()));
            return "Error from %s".formatted(conn.getRemoteSocketAddress());
          }
        }))
        .toArray(CompletableFuture[]::new);

    // Wait for all responses to complete
    CompletableFuture<Void> allDone = CompletableFuture.allOf(futures);
    try {
      allDone.get(5, TimeUnit.SECONDS); // Timeout after 5 seconds
    } catch (Exception e) {
      this.logManager.error("Error waiting for broadcast responses: %s".formatted(e.getMessage()));
    }

    // Collect responses
    StringBuilder responseBuilder = new StringBuilder();
    for (CompletableFuture<String> future : futures) {
      try {
        responseBuilder.append(future.get()).append("\n");
      } catch (Exception e) {
        responseBuilder.append("Error collecting response").append("\n");
      }
    }

    return responseBuilder.toString();
  }

  // Add a default subscriber for a channel
  public void addDefaultSubscriber(String channel, Consumer<String> subscriber) {
    if (!channelSubscriptions.containsKey(channel)) {
      this.logManager.warn("Channel %s does not exist, initializing it.".formatted(channel));
      channelSubscriptions.putIfAbsent(channel, new CopyOnWriteArraySet<>());
    }
    defaultSubscribers.put(channel, subscriber);
    this.logManager.debug("Added default subscriber for channel: %s".formatted(channel));
  }
}
