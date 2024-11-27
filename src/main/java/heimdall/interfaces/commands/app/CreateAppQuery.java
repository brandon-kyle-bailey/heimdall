package heimdall.interfaces.commands.app;

import org.json.JSONObject;

import heimdall.common.abstracts.AQuery;
import heimdall.common.enums.EDomainEvents;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.dtos.app.CreateAppDto;

public class CreateAppQuery implements AQuery<CreateAppDto> {
  private LoggingPort logManager;
  private EventbusAdapter eventbus;

  public CreateAppQuery(LoggingPort logManager, EventbusAdapter eventbus) {
    this.logManager = logManager;
    this.eventbus = eventbus;
  }

  @Override
  public void handle(CreateAppDto event) {
    this.logManager.debug("CreateAppQuery.handle invoked: %s".formatted(event));
    this.eventbus.publish(EDomainEvents.COMMAND_CREATE_APP.toString(), event);
  }
}
