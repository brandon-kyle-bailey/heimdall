package heimdall.modules.activity;

import heimdall.common.enums.EDomainEvents;
import heimdall.core.application.ports.activity.ActivityPort;
import heimdall.core.application.services.activity.UpsertActivityApplicationService;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.commands.activity.UpsertActivityQuery;
import heimdall.interfaces.controllers.activity.UpsertActivityEventController;

public class ActivityModule {
  public static void load(LoggingPort logManager, EventbusAdapter eventBus)
      throws NoSuchMethodException {
    ActivityPort port = new ActivityPort();
    UpsertActivityApplicationService applicationService = new UpsertActivityApplicationService(logManager,
        port);
    UpsertActivityQuery command = new UpsertActivityQuery(logManager, eventBus);
    UpsertActivityEventController handler = new UpsertActivityEventController(logManager, command);

    eventBus.subscribe(EDomainEvents.COMMAND_UPSERT_ACTIVITY.toString(), applicationService);
    eventBus.subscribe(EDomainEvents.CONTROLLER_UPSERT_ACTIVITY.toString(), handler);

    // test publish event
    // UpsertActivityDto event = new UpsertActivityDto("test", "test", "test",
    // LocalDateTime.now(), LocalDateTime.now(),
    // 10000);
    // eventBus.publish(EDomainEvents.CONTROLLER_UPSERT_ACTIVITY.toString(), event);
  }
}
