package heimdall.adapters;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

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
      System.out.println("TCP server started, listening on port " + port);
      while (true) {
        Socket clientSocket = serverSocket.accept(); // Accept client connections
        handleClientConnection(clientSocket);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleClientConnection(Socket clientSocket) {
    // Handle the client connection in a separate method
    try {
      InputStream input = clientSocket.getInputStream();
      OutputStream output = clientSocket.getOutputStream();

      // Example: Reading a message from the client
      byte[] buffer = new byte[1024];
      int bytesRead = input.read(buffer);
      if (bytesRead > 0) {
        String clientMessage = new String(buffer, 0, bytesRead);
        System.out.println("Received message from client: " + clientMessage);
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
