package heimdall;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import heimdall.adapters.WebsocketAdapter;
import heimdall.adapters.EventbusAdapter;
import heimdall.adapters.factories.ActivityTrackerAdapterFactory;
import heimdall.ports.LoggerPort;
import heimdall.ports.ActivityTrackerPort;
import heimdall.common.enums.EDomainEvents;
import heimdall.common.interfaces.IActivityTracker;
import heimdall.handlers.CreateAppEventHandler;
import heimdall.handlers.CreateUserEventHandler;
import heimdall.handlers.UpsertActivityEventHandler;
import heimdall.repositories.AppRepository;
import heimdall.repositories.UserRepository;
import heimdall.repositories.ActivityRepository;

public class App {
  public static void main(String[] args) {
    EventbusAdapter eventbus = new EventbusAdapter();
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    // Initialize repositories
    AppRepository appRepository = new AppRepository();
    UserRepository userRepository = new UserRepository();
    ActivityRepository activityRepository = new ActivityRepository();

    // Subscribe events to their handlers
    eventbus.subscribe(EDomainEvents.CREATE_APP.toString(), new CreateAppEventHandler(appRepository));
    eventbus.subscribe(EDomainEvents.CREATE_USER.toString(), new CreateUserEventHandler(userRepository));
    eventbus.subscribe(EDomainEvents.UPSERT_ACTIVITY.toString(), new UpsertActivityEventHandler(activityRepository));

    // support pub/sub for user creation from client
    String[] defaultChannels = { EDomainEvents.CREATE_USER.toString() };
    WebsocketAdapter ws = new WebsocketAdapter(8080, eventbus, defaultChannels);
    for (String defaultChannel : defaultChannels) {
      ws.addDefaultSubscriber(defaultChannel, message -> {
        LoggerPort.debug(message);
      });

    }
    executorService.submit(ws);

    // Start activity tracker
    try {
      IActivityTracker adapter = ActivityTrackerAdapterFactory.getAdapter(eventbus, ws);
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
