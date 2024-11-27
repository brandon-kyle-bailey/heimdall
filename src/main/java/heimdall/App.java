package heimdall;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import heimdall.modules.EventObserverModule;
import heimdall.ports.ActivityPort;
import heimdall.ports.AppPort;
import heimdall.ports.LoggingPort;
import heimdall.services.ActivityService;
import heimdall.services.AppWatcherService;

public class App {
  public static void main(String[] args) {
    try {

      // System.out.println(FileSystemAdapter.LocalStoragePath());

      // init logger with debug set to true to log messages
      LoggingPort logManager = new LoggingPort(true);

      // init ports
      AppPort appPort = new AppPort();
      ActivityPort activityPort = new ActivityPort();

      // init services
      AppWatcherService appService = new AppWatcherService(logManager, appPort);
      ActivityService activityService = new ActivityService(logManager, activityPort);

      ExecutorService executorService = Executors.newFixedThreadPool(1);

      EventObserverModule eventObserverModule = new EventObserverModule(logManager, appService, activityService);
      executorService.submit(eventObserverModule);

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
