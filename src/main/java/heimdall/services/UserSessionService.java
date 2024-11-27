
package heimdall.services;

import java.sql.SQLException;
import java.util.List;

import heimdall.entities.UserEntity;
import heimdall.ports.LoggingPort;
import heimdall.ports.UserPort;

public class UserSessionService {
  LoggingPort logManager;
  UserPort port;

  public UserSessionService(LoggingPort logManager, UserPort port) {
    this.logManager = logManager;
    this.port = port;
  }

  public void create(String userId, String accountId) {
    this.logManager.debug("UserSessionService.create invoked: %s %s".formatted(userId, accountId));
    try {
      List<UserEntity> foundEntities = this.port.findUsersByAccountIdAndUserId(accountId, userId);
      UserEntity foundEntity = foundEntities.isEmpty() ? null : foundEntities.get(0);
      if (foundEntity != null) {
        this.logManager.debug("User %s already exists.".formatted(userId));
        return;
      }
      UserEntity user = new UserEntity(0, userId, accountId);
      this.port.create(user);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return;
  }
}
