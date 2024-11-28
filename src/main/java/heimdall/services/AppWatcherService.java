package heimdall.services;

import java.sql.SQLException;
import java.util.List;

import heimdall.entities.AppEntity;
import heimdall.ports.AppPort;
import heimdall.ports.LoggingPort;

public class AppWatcherService {
  LoggingPort logManager;
  AppPort port;

  public AppWatcherService(LoggingPort logManager, AppPort port) {
    this.logManager = logManager;
    this.port = port;
  }

  public void create(String appName) {
    this.logManager.debug("AppWatcherService.create invoked: %s".formatted(appName));
    try {
      List<AppEntity> foundApps = this.port.findByName(appName);
      AppEntity foundApp = foundApps.isEmpty() ? null : foundApps.get(0);
      if (foundApp != null) {
        this.logManager.debug("App %s already exists.".formatted(appName));
        return;
      }
      this.logManager.debug("Creating App %s.".formatted(appName));
      AppEntity app = new AppEntity(0, appName);
      this.port.create(app);
    } catch (SQLException e) {
      this.logManager.error(e.getMessage());
    }
    return;
  }
}
