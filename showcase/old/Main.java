package heimdall;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import heimdall.common.interfaces.IActivityTracker;
import heimdall.infrastructure.adapters.drivers.PersistenceDriverAdapter;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.adapters.logging.LoggingAdapter;
import heimdall.infrastructure.adapters.system.ActivityTrackerAdapterFactory;
import heimdall.infrastructure.ports.activity.ActivityTrackerPort;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.modules.activity.ActivityModule;
import heimdall.modules.app.AppModule;
import heimdall.modules.user.UserModule;

public class App {
  public static void main(String[] args) throws NoSuchMethodException {
    // executor service for concurrency
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    LoggingAdapter loggingAdapter = new LoggingAdapter(true);
    LoggingPort logManager = new LoggingPort(loggingAdapter);
    logManager.debug(PersistenceDriverAdapter.DB_URI);
    EventbusAdapter eventBus = new EventbusAdapter(logManager);
    UserModule.load(logManager, eventBus);
    AppModule appModule = new AppModule(logManager, eventBus);
    ActivityModule.load(logManager, eventBus);

    try {
      executorService.submit(appModule);
      // // support pub/sub for user creation from client
      // String[] defaultChannels = { EDomainEvents.CONTROLLER_CREATE_USER.toString()
      // };
      // WebsocketAdapter ws = new WebsocketAdapter(logManager, 8080, eventBus,
      // defaultChannels);
      // for (String defaultChannel : defaultChannels) {
      // ws.addDefaultSubscriber(defaultChannel, message -> {
      // logManager.debug(message);
      // });
      //
      // }
      // executorService.submit(ws);
      IActivityTracker adapter = ActivityTrackerAdapterFactory.getAdapter(logManager, eventBus);
      ActivityTrackerPort tracker = new ActivityTrackerPort(adapter);
      executorService.submit(tracker);
    } catch (Exception e) {
      logManager.error(e.getMessage());
      return;
    }
    // Add shutdown hook to gracefully shut down the executor when the app exits
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      logManager.debug("Shutting down...");
      executorService.shutdown(); // Stop the executor service gracefully
    }));
  }
}
