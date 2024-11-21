
package heimdall.handlers;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONObject;

import heimdall.common.interfaces.IEventHandler;
import heimdall.ports.LoggerPort;
import heimdall.dtos.AppDto;
import heimdall.dtos.mappers.AppDtoMapper;
import heimdall.repositories.AppRepository;

public class CreateAppEventHandler implements IEventHandler {
  private AppRepository repository;

  public CreateAppEventHandler(AppRepository repository) {
    this.repository = repository;
  }

  @Override
  public Object handle(JSONObject event) {
    LoggerPort.debug("CreateAppEventHandler.handle invoked: %s".formatted(event));
    try {
      event.put("id", 0);
      AppDto app = AppDtoMapper.interfaceToDomain(event);
      List<AppDto> foundApps = repository.findByName(app.getName());
      AppDto foundApp = foundApps.isEmpty() ? null : foundApps.get(0);
      if (foundApp != null) {
        LoggerPort.debug("App %s already exists.".formatted(app.getName()));
        return null;
      }
      repository.create(app);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return event;
  }

}
