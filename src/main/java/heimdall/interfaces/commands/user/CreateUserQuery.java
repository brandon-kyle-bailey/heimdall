package heimdall.interfaces.commands.user;

import heimdall.common.abstracts.AQuery;
import heimdall.common.enums.EDomainEvents;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.dtos.user.CreateUserDto;

public class CreateUserQuery implements AQuery<CreateUserDto> {
  private LoggingPort logManager;
  private EventbusAdapter eventbus;

  public CreateUserQuery(LoggingPort logManager, EventbusAdapter eventbus) throws NoSuchMethodException {
    this.logManager = logManager;
    this.eventbus = eventbus;
  }

  @Override
  public void handle(CreateUserDto event) {
    this.logManager.debug("CreateUserQuery.handle invoked: %s".formatted(event));
    this.eventbus.publish(EDomainEvents.COMMAND_CREATE_USER.toString(), event);
  }
}
