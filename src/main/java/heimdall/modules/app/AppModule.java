package heimdall.modules.app;

import heimdall.common.enums.EDomainEvents;
import heimdall.core.application.ports.app.AppPort;
import heimdall.core.application.services.app.CreateAppApplicationService;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.commands.app.CreateAppQuery;
import heimdall.interfaces.controllers.app.CreateAppEventController;
import heimdall.interfaces.dtos.app.CreateAppDto;

public class AppModule {
  public static void load(LoggingPort logManager, EventbusAdapter eventBus)
      throws NoSuchMethodException {
    AppPort port = new AppPort();
    CreateAppApplicationService applicationService = new CreateAppApplicationService(logManager, port);
    CreateAppQuery command = new CreateAppQuery(logManager, eventBus);
    CreateAppEventController handler = new CreateAppEventController(logManager, command);

    eventBus.subscribe(EDomainEvents.COMMAND_CREATE_APP.toString(), applicationService);
    eventBus.subscribe(EDomainEvents.CONTROLLER_CREATE_APP.toString(), handler);

    // test publish event
    CreateAppDto event = new CreateAppDto("test");
    eventBus.publish(EDomainEvents.CONTROLLER_CREATE_APP.toString(), event);
  }
}
