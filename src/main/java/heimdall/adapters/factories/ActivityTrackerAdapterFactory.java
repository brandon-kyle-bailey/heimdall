package heimdall.adapters.factories;

import heimdall.common.interfaces.IActivityTracker;
import heimdall.ports.LoggerPort;
import heimdall.adapters.MacActivityAdapter;
import heimdall.adapters.EventbusAdapter;

public class ActivityTrackerAdapterFactory {
  public static IActivityTracker getAdapter(EventbusAdapter eventbus) throws Exception {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) {
      return new MacActivityAdapter(eventbus);
    } else if (osName.contains("mac")) {
      return new MacActivityAdapter(eventbus);
    } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
      return new MacActivityAdapter(eventbus);
    } else {
      LoggerPort.error("Unkown operating system: %s".formatted(osName));
      throw new Exception("Unknown operating system");
    }
  }
}
