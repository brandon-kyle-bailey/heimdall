package heimdall.modules;

import heimdall.ports.EventObserverPort;
import heimdall.ports.LoggingPort;
import heimdall.services.ActivityService;
import heimdall.services.AppWatcherService;

public class EventObserverModule implements Runnable {
  LoggingPort logManager;
  AppWatcherService appService;
  ActivityService activityService;

  public EventObserverModule(LoggingPort logManager, AppWatcherService appService, ActivityService activityService) {
    this.logManager = logManager;
    this.appService = appService;
    this.activityService = activityService;
  }

  @Override
  public void run() {
    EventObserverPort.run(this.logManager, this.appService, this.activityService);
  }
}
