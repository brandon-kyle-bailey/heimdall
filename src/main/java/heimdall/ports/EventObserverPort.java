package heimdall.ports;

import heimdall.common.interfaces.IEventObserverDriver;
import heimdall.factories.EventObserverPortFactory;
import heimdall.services.ActivityService;
import heimdall.services.AppWatcherService;

public class EventObserverPort {
  public static void run(LoggingPort logManager, AppWatcherService appService, ActivityService activityService) {
    try {
      IEventObserverDriver driver = EventObserverPortFactory.getOperatingSystemDriver(logManager, appService,
          activityService);
      driver.run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
