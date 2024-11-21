package heimdall;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import heimdall.adapters.TcpAdapter;
import heimdall.adapters.EventbusAdapter;
import heimdall.adapters.factories.ActivityTrackerAdapterFactory;
import heimdall.ports.LoggerPort;
import heimdall.ports.ActivityTrackerPort;
import heimdall.common.enums.EDomainEvents;
import heimdall.common.interfaces.IActivityTracker;
import heimdall.handlers.CreateAppEventHandler;
import heimdall.handlers.CreateUserEventHandler;
import heimdall.handlers.UpsertActivityEventHandler;

public class App {
  public static void main(String[] args) {
    EventbusAdapter eventbus = new EventbusAdapter();
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    // Subscribe events to their handlers
    eventbus.subscribe(EDomainEvents.CREATE_APP.toString(), new CreateAppEventHandler());
    eventbus.subscribe(EDomainEvents.CREATE_USER.toString(), new CreateUserEventHandler());
    eventbus.subscribe(EDomainEvents.UPSERT_ACTIVITY.toString(), new UpsertActivityEventHandler());

    // Start TCP server
    TcpAdapter server = new TcpAdapter(8080, eventbus);
    executorService.submit(server);

    // Start activity tracker
    try {
      IActivityTracker adapter = ActivityTrackerAdapterFactory.getAdapter(eventbus);
      ActivityTrackerPort tracker = new ActivityTrackerPort(adapter);
      executorService.submit(tracker);
    } catch (Exception e) {
      LoggerPort.error(e.getMessage());
      return;
    }

    // Add shutdown hook to gracefully shut down the executor when the app exits
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      LoggerPort.debug("Shutting down...");
      executorService.shutdown(); // Stop the executor service gracefully
    }));
  }
}
