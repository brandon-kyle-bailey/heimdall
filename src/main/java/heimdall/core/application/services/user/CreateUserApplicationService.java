package heimdall.core.application.services.user;

import org.json.JSONObject;

import heimdall.common.abstracts.AApplicationService;
import heimdall.core.domain.entities.user.UserEntity;
import heimdall.infrastructure.adapters.persistence.PersistenceAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;

public class CreateUserApplicationService extends AApplicationService<UserEntity> {

  public CreateUserApplicationService(LoggingPort logManager, PersistenceAdapter<UserEntity> port)
      throws NoSuchMethodException {
    super(logManager, port);
  }

  @Override
  public void handle(JSONObject event) {
    this.logManager.debug("CreateUserApplicationService.handle invoked: %s".formatted(event));
  }
}
