package heimdall;

import heimdall.adapters.WindowAdapter;
import heimdall.adapters.EventbusAdapter;
import common.shared.enumerator.EMessageKey;
import heimdall.handlers.CreateApplicationEventHandler;
import heimdall.handlers.CreateActivityEventHandler;
import heimdall.repositories.ApplicationPersistenceRepository;
import heimdall.repositories.ActivityPersistenceRepository;

public class App {
  public static void main(String[] args) {
    EventbusAdapter eventBus = new EventbusAdapter();
    ApplicationPersistenceRepository applicationPersistenceRepository = new ApplicationPersistenceRepository();
    ActivityPersistenceRepository activityPersistenceRepository = new ActivityPersistenceRepository();
    eventBus.subscribe(EMessageKey.CREATE_APPLICATION.toString(),
        new CreateApplicationEventHandler(applicationPersistenceRepository));
    eventBus.subscribe(EMessageKey.CREATE_ACTIVITY.toString(),
        new CreateActivityEventHandler(activityPersistenceRepository));
    WindowAdapter windowAdapter = new WindowAdapter(eventBus);
    windowAdapter.run();
  }
}
