package heimdall.core.application.services.user;

import java.sql.SQLException;
import java.util.List;

import heimdall.common.abstracts.AApplicationService;
import heimdall.core.application.ports.user.UserPort;
import heimdall.core.domain.entities.user.UserEntity;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.dtos.user.CreateUserDto;

public class CreateUserApplicationService implements AApplicationService<CreateUserDto> {
  private LoggingPort logManager;
  private UserPort port;

  public CreateUserApplicationService(LoggingPort logManager, UserPort port) {
    this.logManager = logManager;
    this.port = port;
  }

  @Override
  public void handle(CreateUserDto event) {
    this.logManager.debug("CreateUserApplicationService.handle invoked: %s".formatted(event));
    try {
      List<UserEntity> foundEntities = this.port.findUsersByAccountIdAndUserId(event.getAccountId(), event.getUserId());
      UserEntity foundEntity = foundEntities.isEmpty() ? null : foundEntities.get(0);
      if (foundEntity != null) {
        this.logManager.debug("User %s already exists.".formatted(event.getUserId()));
        return;
      }
      UserEntity user = new UserEntity(0, event.getUserId(), event.getAccountId());
      this.port.create(user);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return;
  }
}
