package heimdall.handlers;

import java.sql.SQLException;
import java.util.List;

import heimdall.dtos.CreateApplicationDto;
import heimdall.repositories.ApplicationPersistenceRepository;
import heimdall.repositories.models.ApplicationModel;

public class CreateApplicationEventHandler {
  ApplicationPersistenceRepository repository;

  public CreateApplicationEventHandler(ApplicationPersistenceRepository repository) {
    this.repository = repository;
  }

  public ApplicationModel handle(CreateApplicationDto event) {
    System.out.println("CreateApplicationEventHandler.handle invoked: %s".formatted(event.getName()));
    ApplicationModel model = new ApplicationModel(0, event.getName());
    try {
      List<ApplicationModel> models = this.repository.findByName(event.getName());
      ApplicationModel foundModel = models.isEmpty() ? null : models.get(0);
      if (foundModel != null) {
        System.out.println("App already exists.");
        return null;
      }
      this.repository.create(model);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return model;
  }
}
