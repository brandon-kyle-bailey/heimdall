package heimdall.interfaces.commands.app;

import org.json.JSONObject;

import heimdall.common.abstracts.AQuery;
import heimdall.infrastructure.ports.logging.LoggingPort;

public class CreateAppQuery extends AQuery {

  public CreateAppQuery(LoggingPort logManager) throws NoSuchMethodException {
    super(logManager);
  }

  @Override
  public void handle(JSONObject event) {
    this.logManager.debug("CreateAppQuery.handle invoked: %s".formatted(event));

  }
}
