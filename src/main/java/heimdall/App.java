package heimdall;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import heimdall.adapters.TcpAdapter;
import heimdall.adapters.EventbusAdapter;
import heimdall.ports.LoggerPort;

public class App {
  public static void main(String[] args) {
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    EventbusAdapter eventbus = new EventbusAdapter();

    // Start TCP server
    TcpAdapter server = new TcpAdapter(8080, eventbus);
    executorService.submit(server);

    // Add shutdown hook to gracefully shut down the executor when the app exits
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      LoggerPort.debug("Shutting down...");
      executorService.shutdown(); // Stop the executor service gracefully
    }));
  }
}
