package heimdall.handlers;

import java.sql.SQLException;
import java.util.List;

import heimdall.dtos.CreateActivityDto;
import heimdall.repositories.ActivityPersistenceRepository;
import heimdall.repositories.models.ActivityModel;

public class CreateActivityEventHandler {
  ActivityPersistenceRepository repository;

  public CreateActivityEventHandler(ActivityPersistenceRepository repository) {
    this.repository = repository;
  }

  public ActivityModel handle(CreateActivityDto event) {
    System.out.println("CreateActivityEventHandler.handle invoked: %s %s %s %s %s".formatted(event.getAppName(),
        event.getTitle(), event.getUrl(), event.getStartTime(), event.getEndTime()));
    try {
      ActivityModel newModel = new ActivityModel(0, event.getAppName(), event.getTitle(), event.getUrl(),
          event.getStartTime(), event.getEndTime());
      List<ActivityModel> models = this.repository.findLatestByAppNameTitleUrl(event.getAppName(), event.getTitle(),
          event.getUrl());
      ActivityModel foundModel = models.isEmpty() ? null : models.get(0);
      if (foundModel != null) {
        System.out.println("Activity found. Upserting: %s -> %s | %s -> %s".formatted(newModel.getStartTime(),
            foundModel.getStartTime(), newModel.getEndTime(), event.getEndTime()));
        newModel.setStartTime(foundModel.getStartTime());
        newModel.setEndTime(event.getEndTime());
        newModel.setId(foundModel.getId());
        System.out.println("Duration: %s".formatted(newModel.getDuration()));
        this.repository.update(newModel);
        return newModel;
      }
      System.out.println("No Activity found. Creating");
      this.repository.create(newModel);
      return newModel;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
