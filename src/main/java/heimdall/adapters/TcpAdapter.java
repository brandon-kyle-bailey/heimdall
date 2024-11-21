package heimdall.adapters;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;

import heimdall.ports.LoggerPort;
import heimdall.common.enums.EDomainEvents;

public class TcpAdapter implements Runnable {
  private int port; // Port to listen on
  private EventbusAdapter eventbus;

  public TcpAdapter(int port, EventbusAdapter eventbus) {
    this.port = port;
    this.eventbus = eventbus;
  }

  @Override
  public void run() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      LoggerPort.debug("TCP server started, listening on port %s".formatted(port));
      while (true) {
        Socket clientSocket = serverSocket.accept(); // Accept client connections
        handleClientConnection(clientSocket);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleMessage(String message) {
    LoggerPort.debug("Received tcp message: %s".formatted(message));
    JSONObject jsonEvent = new JSONObject(message);
    String event = jsonEvent.getString("event");

    String domainEvent = null;
    if (event.equals(EDomainEvents.CREATE_USER.toString())) {
      domainEvent = EDomainEvents.CREATE_USER.toString();
    } else {
      LoggerPort.warn("Unrecognized message: %s".formatted(message));
      return;
    }
    JSONObject payload = jsonEvent.getJSONObject("payload");
    this.eventbus.publish(domainEvent, payload);
  }

  private void handleClientConnection(Socket clientSocket) {
    // Handle the client connection in a separate method
    try {
      InputStream input = clientSocket.getInputStream();
      OutputStream output = clientSocket.getOutputStream();

      byte[] buffer = new byte[1024];
      int bytesRead = input.read(buffer);
      if (bytesRead > 0) {
        String clientMessage = new String(buffer, 0, bytesRead).trim();
        LoggerPort.debug("Received message from client: %s".formatted(clientMessage));
        this.handleMessage(clientMessage);
      }

      // Sending a response back to the client
      String response = "Message received\n";
      output.write(response.getBytes());

      clientSocket.close(); // Close the client connection
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
