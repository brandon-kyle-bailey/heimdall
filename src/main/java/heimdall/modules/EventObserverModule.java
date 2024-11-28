package heimdall.modules;

import heimdall.adapters.WebsocketAdapter;
import heimdall.ports.EventObserverPort;
import heimdall.ports.LoggingPort;
import heimdall.services.ActivityService;
import heimdall.services.AppWatcherService;

public class EventObserverModule implements Runnable {
  LoggingPort logManager;
  AppWatcherService appService;
  ActivityService activityService;
  WebsocketAdapter websocket;

  public EventObserverModule(LoggingPort logManager, AppWatcherService appService, ActivityService activityService,
      WebsocketAdapter websocket) {
    this.logManager = logManager;
    this.appService = appService;
    this.activityService = activityService;
    this.websocket = websocket;
  }

  @Override
  public void run() {
    EventObserverPort.run(this.logManager, this.appService, this.activityService, this.websocket);
  }
}
