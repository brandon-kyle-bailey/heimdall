package heimdall;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import heimdall.adapters.EventbusAdapter;
import heimdall.adapters.FileSystemAdapter;
import heimdall.adapters.WebsocketAdapter;
import heimdall.handlers.CreateUserEventHandler;
import heimdall.modules.EventObserverModule;
import heimdall.ports.ActivityPort;
import heimdall.ports.AppPort;
import heimdall.ports.LoggingPort;
import heimdall.ports.UserPort;
import heimdall.services.ActivityService;
import heimdall.services.AppWatcherService;
import heimdall.services.UserSessionService;

public class App {
  public static void main(String[] args) {
    try {

      // init logger with debug set to true to log messages
      LoggingPort logManager = new LoggingPort(true);

      logManager.debug(FileSystemAdapter.LocalStoragePath());

      EventbusAdapter eventbus = new EventbusAdapter(logManager);

      // init ports
      AppPort appPort = new AppPort();
      UserPort userPort = new UserPort();
      ActivityPort activityPort = new ActivityPort();

      // init services
      AppWatcherService appService = new AppWatcherService(logManager, appPort);
      ActivityService activityService = new ActivityService(logManager, activityPort);
      UserSessionService userService = new UserSessionService(logManager, userPort);

      ExecutorService executorService = Executors.newFixedThreadPool(2);

      CreateUserEventHandler createUserEventHandler = new CreateUserEventHandler(logManager, userService);
      eventbus.subscribe("CREATE_USER", createUserEventHandler);

      WebsocketAdapter ws = new WebsocketAdapter(logManager, eventbus, 8080);

      EventObserverModule eventObserverModule = new EventObserverModule(logManager, appService, activityService,
          ws);

      executorService.submit(eventObserverModule);
      executorService.submit(ws);

      // Add shutdown hook to gracefully shut down the executor when the app exits
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        logManager.debug("Shutting down...");
        executorService.shutdown(); // Stop the executor service gracefully
      }));
    } catch (Exception e) {
      e.getStackTrace();
    }
  }
}
