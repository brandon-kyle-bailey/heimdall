package heimdall.modules.app;

import heimdall.common.enums.EDomainEvents;
import heimdall.core.application.ports.app.AppPort;
import heimdall.core.application.services.app.CreateAppApplicationService;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.commands.app.CreateAppQuery;
import heimdall.interfaces.controllers.app.CreateAppEventController;

public class AppModule implements Runnable {
  private LoggingPort logManager;
  private EventbusAdapter eventBus;

  public AppModule(LoggingPort logManager, EventbusAdapter eventBus) {
    this.logManager = logManager;
    this.eventBus = eventBus;
  }

  @Override
  public void run() {

    AppPort port = new AppPort();
    CreateAppApplicationService applicationService = new CreateAppApplicationService(this.logManager, port);
    CreateAppQuery command = new CreateAppQuery(this.logManager, this.eventBus);
    CreateAppEventController handler = new CreateAppEventController(this.logManager, command);

    this.eventBus.subscribe(EDomainEvents.COMMAND_CREATE_APP.toString(), applicationService);
    this.eventBus.subscribe(EDomainEvents.CONTROLLER_CREATE_APP.toString(), handler);

    // test publish event
    // CreateAppDto event = new CreateAppDto("test");
    // eventBus.publish(EDomainEvents.CONTROLLER_CREATE_APP.toString(), event);
  }
}
