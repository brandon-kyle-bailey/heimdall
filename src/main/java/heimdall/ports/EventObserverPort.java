package heimdall.ports;

import heimdall.adapters.WebsocketAdapter;
import heimdall.common.interfaces.IEventObserverDriver;
import heimdall.factories.EventObserverPortFactory;
import heimdall.services.ActivityService;
import heimdall.services.AppWatcherService;

public class EventObserverPort {
  public static void run(LoggingPort logManager, AppWatcherService appService, ActivityService activityService,
      WebsocketAdapter websocket) {
    try {
      IEventObserverDriver driver = EventObserverPortFactory.getOperatingSystemDriver(logManager, appService,
          activityService, websocket);
      driver.run();
    } catch (Exception e) {
      logManager.error(e.getMessage());
    }
  }
}
