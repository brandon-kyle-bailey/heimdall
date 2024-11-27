package heimdall.interfaces.controllers.app;

import org.json.JSONObject;

import heimdall.common.abstracts.AEventHandler;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.commands.app.CreateAppQuery;
import heimdall.interfaces.dtos.app.CreateAppDto;

public class CreateAppEventController implements AEventHandler<CreateAppDto> {
  private LoggingPort logManager;
  private CreateAppQuery query;

  public CreateAppEventController(LoggingPort logManager, CreateAppQuery query) {
    this.logManager = logManager;
    this.query = query;
  }

  @Override
  public void handle(CreateAppDto event) {
    this.logManager.debug("CreateAppEventController.handle invoked: %s".formatted(event));
    this.query.handle(event);
  }
}
