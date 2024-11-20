package heimdall.adapters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

import heimdall.dtos.RegisterUserDto;

public class TcpAdapter {
  EventbusAdapter eventBus;

  public TcpAdapter(EventbusAdapter eventBus) {
    this.eventBus = eventBus;
  }

  private void handleMessage(String message) {
    System.err.println("Received tcp message: %s".formatted(message));
    JSONObject jsonEvent = new JSONObject(message);
    String event = jsonEvent.getString("event");
    if (event.equals("REGISTER_USER")) {
      JSONObject payload = jsonEvent.getJSONObject("payload");
      RegisterUserDto dto = new RegisterUserDto(payload.getString("userId"),
          payload.getString("accountId"));
      this.eventBus.publish("REGISTER_USER", dto);
    } else {
      System.out.println("Unrecognized message: " + message);
    }
  }

  public void init() {
    try (ServerSocket serverSocket = new ServerSocket(12345)) {
      System.out.println("Server ready on port 12345");
      while (true) {
        Socket socket = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String message = reader.readLine();
        this.handleMessage(message);
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
