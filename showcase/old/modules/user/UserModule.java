package heimdall.modules.user;

import heimdall.common.enums.EDomainEvents;
import heimdall.core.application.ports.user.UserPort;
import heimdall.core.application.services.user.CreateUserApplicationService;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.commands.user.CreateUserQuery;
import heimdall.interfaces.controllers.user.CreateUserEventController;

public class UserModule {
  public static void load(LoggingPort logManager, EventbusAdapter eventBus)
      throws NoSuchMethodException {
    UserPort userPort = new UserPort();
    CreateUserApplicationService createUserApplicationService = new CreateUserApplicationService(logManager, userPort);
    CreateUserQuery createUserQuery = new CreateUserQuery(logManager, eventBus);
    CreateUserEventController createUserEventController = new CreateUserEventController(logManager, createUserQuery);

    eventBus.subscribe(EDomainEvents.COMMAND_CREATE_USER.toString(), createUserApplicationService);
    eventBus.subscribe(EDomainEvents.CONTROLLER_CREATE_USER.toString(), createUserEventController);

    // test publish event
    // CreateUserDto event = new CreateUserDto("test", "test");
    // eventBus.publish(EDomainEvents.CONTROLLER_CREATE_USER.toString(), event);
  }
}
