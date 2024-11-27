package heimdall.interfaces.controllers.user;

import org.json.JSONObject;

import heimdall.common.abstracts.AEventHandler;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.commands.user.CreateUserQuery;
import heimdall.interfaces.dtos.user.CreateUserDto;

public class CreateUserEventController implements AEventHandler<CreateUserDto> {
  private LoggingPort logManager;
  private CreateUserQuery query;

  public CreateUserEventController(LoggingPort logManager, CreateUserQuery query) {
    this.logManager = logManager;
    this.query = query;
  }

  @Override
  public void handle(CreateUserDto event) {
    this.logManager.debug("CreateUserEventController.handle invoked: %s".formatted(event));
    this.query.handle(event);
  }
}
