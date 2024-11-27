package heimdall.interfaces.commands.activity;

import heimdall.common.abstracts.AQuery;
import heimdall.common.enums.EDomainEvents;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.dtos.activity.UpsertActivityDto;

public class UpsertActivityQuery implements AQuery<UpsertActivityDto> {
  private LoggingPort logManager;
  private EventbusAdapter eventbus;

  public UpsertActivityQuery(LoggingPort logManager, EventbusAdapter eventbus) {
    this.logManager = logManager;
    this.eventbus = eventbus;
  }

  @Override
  public void handle(UpsertActivityDto event) {
    this.logManager.debug("UpsertActivityQuery.handle invoked: %s".formatted(event));
    this.eventbus.publish(EDomainEvents.COMMAND_UPSERT_ACTIVITY.toString(), event);
  }
}
