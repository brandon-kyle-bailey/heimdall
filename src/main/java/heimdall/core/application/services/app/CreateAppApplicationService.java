package heimdall.core.application.services.app;

import org.json.JSONObject;

import heimdall.common.abstracts.AApplicationService;
import heimdall.core.domain.entities.app.AppEntity;
import heimdall.infrastructure.adapters.persistence.PersistenceAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;

public class CreateAppApplicationService extends AApplicationService<AppEntity> {

  public CreateAppApplicationService(LoggingPort logManager, PersistenceAdapter<AppEntity> port)
      throws NoSuchMethodException {
    super(logManager, port);
  }

  @Override
  public void handle(JSONObject event) {
    this.logManager.debug("CreateAppApplicationService.handle invoked: %s".formatted(event));
  }
}
