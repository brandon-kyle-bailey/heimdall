package heimdall.core.application.services.app;

import java.sql.SQLException;
import java.util.List;

import heimdall.common.abstracts.AApplicationService;
import heimdall.core.application.ports.app.AppPort;
import heimdall.core.domain.entities.app.AppEntity;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.dtos.app.CreateAppDto;

public class CreateAppApplicationService implements AApplicationService<CreateAppDto> {
  private LoggingPort logManager;
  private AppPort port;

  public CreateAppApplicationService(LoggingPort logManager, AppPort port) {
    this.logManager = logManager;
    this.port = port;
  }

  @Override
  public void handle(CreateAppDto event) {
    this.logManager.debug("CreateAppApplicationService.handle invoked: %s".formatted(event));
    try {
      List<AppEntity> foundApps = this.port.findByName(event.getName());
      AppEntity foundApp = foundApps.isEmpty() ? null : foundApps.get(0);
      if (foundApp != null) {
        this.logManager.debug("App %s already exists.".formatted(event.getName()));
        return;
      }
      AppEntity app = new AppEntity(0, event.getName());
      this.port.create(app);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return;
  }
}
