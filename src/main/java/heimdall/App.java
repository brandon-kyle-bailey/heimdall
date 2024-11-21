package heimdall;

import heimdall.adapters.WindowAdapter;
import heimdall.adapters.EventbusAdapter;
import heimdall.adapters.TcpAdapter;
import common.shared.enumerator.EMessageKey;
import heimdall.handlers.CreateApplicationEventHandler;
import heimdall.handlers.CreateActivityEventHandler;
import heimdall.handlers.RegisterUserEventHandler;
import heimdall.repositories.ApplicationPersistenceRepository;
import heimdall.repositories.UserPersistenceRepository;
import heimdall.repositories.ActivityPersistenceRepository;

public class App {
  public static void main(String[] args) {
    EventbusAdapter eventBus = new EventbusAdapter();

    TcpAdapter tcpServer = new TcpAdapter(eventBus);

    UserPersistenceRepository userPersistenceRepository = new UserPersistenceRepository();
    eventBus.subscribe(EMessageKey.REGISTER_USER.toString(),
        new RegisterUserEventHandler(userPersistenceRepository));

    ApplicationPersistenceRepository applicationPersistenceRepository = new ApplicationPersistenceRepository();
    eventBus.subscribe(EMessageKey.CREATE_APPLICATION.toString(),
        new CreateApplicationEventHandler(applicationPersistenceRepository));

    ActivityPersistenceRepository activityPersistenceRepository = new ActivityPersistenceRepository();
    eventBus.subscribe(EMessageKey.CREATE_ACTIVITY.toString(),
        new CreateActivityEventHandler(activityPersistenceRepository));

    WindowAdapter windowAdapter = new WindowAdapter(eventBus);
    Thread tcpServerThread = new Thread(() -> {
      // Your TCP server logic here
      tcpServer.init();
    });
    Thread windowAdapterThread = new Thread(() -> {
      windowAdapter.run();
    });

    tcpServerThread.start();
    windowAdapterThread.start();
  }
}
