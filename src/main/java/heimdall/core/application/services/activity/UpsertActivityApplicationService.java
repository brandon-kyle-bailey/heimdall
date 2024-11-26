package heimdall.core.application.services.activity;

import org.json.JSONObject;

import heimdall.common.abstracts.AApplicationService;
import heimdall.core.domain.entities.activity.ActivityEntity;
import heimdall.infrastructure.adapters.persistence.PersistenceAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;

public class UpsertActivityApplicationService extends AApplicationService<ActivityEntity> {

  public UpsertActivityApplicationService(LoggingPort logManager, PersistenceAdapter<ActivityEntity> port)
      throws NoSuchMethodException {
    super(logManager, port);
  }

  @Override
  public void handle(JSONObject event) {
    this.logManager.debug("UpsertActivityApplicationService.handle invoked: %s".formatted(event));
  }
}
