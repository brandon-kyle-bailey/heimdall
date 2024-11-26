package heimdall.infrastructure.adapters.system;

import heimdall.common.interfaces.IActivityTracker;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.adapters.system.linux.LinuxActivityAdapter;
import heimdall.infrastructure.adapters.system.macos.MacosActivityAdapter;
import heimdall.infrastructure.adapters.system.windows.WindowsActivityAdapter;
import heimdall.infrastructure.adapters.websockets.WebsocketAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;

public class ActivityTrackerAdapterFactory {
  public static IActivityTracker getAdapter(LoggingPort logManager, EventbusAdapter eventbus,
      WebsocketAdapter websocket) throws Exception {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) {
      return new WindowsActivityAdapter(logManager, eventbus, websocket);
    } else if (osName.contains("mac")) {
      return new MacosActivityAdapter(logManager, eventbus, websocket);
    } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
      return new LinuxActivityAdapter(logManager, eventbus, websocket);
    } else {
      logManager.error("Unkown operating system: %s".formatted(osName));
      throw new Exception("Unknown operating system");
    }
  }
}
