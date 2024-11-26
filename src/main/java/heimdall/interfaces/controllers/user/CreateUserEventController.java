package heimdall.interfaces.controllers.user;

import org.json.JSONObject;

import heimdall.common.abstracts.AEventHandler;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.commands.user.CreateUserQuery;

public class CreateUserEventController extends AEventHandler {

  public CreateUserEventController(LoggingPort logManager, CreateUserQuery query) throws NoSuchMethodException {
    super(logManager, query);
  }

  @Override
  public void handle(JSONObject event) {
    this.logManager.debug("CreateUserEventController.handle invoked: %s".formatted(event));
    this.query.handle(event);
  }
}
