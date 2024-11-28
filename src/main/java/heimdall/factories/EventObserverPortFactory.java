package heimdall.factories;

import heimdall.adapters.FileSystemAdapter;
import heimdall.adapters.WebsocketAdapter;
import heimdall.common.interfaces.IEventObserverDriver;
import heimdall.drivers.LinuxEventObserverDriver;
import heimdall.drivers.MacosEventObserverDriver;
import heimdall.drivers.WindowsEventObserverDriver;
import heimdall.ports.LoggingPort;
import heimdall.services.ActivityService;
import heimdall.services.AppWatcherService;

public class EventObserverPortFactory {
  public static IEventObserverDriver getOperatingSystemDriver(LoggingPort logManager, AppWatcherService appService,
      ActivityService activityService, WebsocketAdapter websocket)
      throws Exception {
    String osName = FileSystemAdapter.getOperatingSystem();
    if (osName.contains("win")) {
      return new WindowsEventObserverDriver(logManager, appService, activityService);
    } else if (osName.contains("mac")) {
      return new MacosEventObserverDriver(logManager, appService, activityService, websocket);
    } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
      return new LinuxEventObserverDriver(logManager, appService, activityService);
    } else {
      throw new Exception("Unknown operating system");
    }
  }
}
