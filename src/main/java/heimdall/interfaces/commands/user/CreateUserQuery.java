package heimdall.interfaces.commands.user;

import org.json.JSONObject;

import heimdall.common.abstracts.AQuery;
import heimdall.infrastructure.ports.logging.LoggingPort;

public class CreateUserQuery extends AQuery {

  public CreateUserQuery(LoggingPort logManager) throws NoSuchMethodException {
    super(logManager);
  }

  @Override
  public void handle(JSONObject event) {
    this.logManager.debug("CreateUserQuery.handle invoked: %s".formatted(event));

  }
}
