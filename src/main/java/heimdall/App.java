package heimdall;

import heimdall.infrastructure.adapters.drivers.PersistenceDriverAdapter;
import heimdall.infrastructure.adapters.events.EventbusAdapter;
import heimdall.infrastructure.adapters.logging.LoggingAdapter;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.modules.activity.ActivityModule;
import heimdall.modules.app.AppModule;
import heimdall.modules.user.UserModule;

public class App {
  public static void main(String[] args) throws NoSuchMethodException {
    LoggingAdapter loggingAdapter = new LoggingAdapter(true);
    LoggingPort logManager = new LoggingPort(loggingAdapter);
    logManager.debug(PersistenceDriverAdapter.DB_URI);
    EventbusAdapter eventBus = new EventbusAdapter(logManager);
    UserModule.load(logManager, eventBus);
    AppModule.load(logManager, eventBus);
    ActivityModule.load(logManager, eventBus);
  }
}
