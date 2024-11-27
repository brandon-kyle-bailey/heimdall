package heimdall.core.application.services.activity;

import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import heimdall.common.abstracts.AApplicationService;
import heimdall.core.application.ports.activity.ActivityPort;
import heimdall.core.domain.entities.activity.ActivityEntity;
import heimdall.infrastructure.ports.logging.LoggingPort;
import heimdall.interfaces.dtos.activity.UpsertActivityDto;

public class UpsertActivityApplicationService implements AApplicationService<UpsertActivityDto> {
  private LoggingPort logManager;
  private ActivityPort port;

  public UpsertActivityApplicationService(LoggingPort logManager, ActivityPort port) {
    this.logManager = logManager;
    this.port = port;
  }

  @Override
  public void handle(UpsertActivityDto event) {
    this.logManager.debug("UpsertActivityApplicationService.handle invoked: %s".formatted(event));
    try {
      List<ActivityEntity> foundActivities = this.port.findLatestByNameTitleUrl(event.getApp(), event.getTitle(),
          event.getUrl());
      ActivityEntity foundActivity = foundActivities.isEmpty() ? null : foundActivities.get(0);
      if (foundActivity != null) {
        // update the found activities end time for duration
        foundActivity.setEndTime(event.getEndTime());
        if (foundActivity.getStartTime() != null && foundActivity.getEndTime() != null) {
          int duration = (int) ChronoUnit.MILLIS.between(foundActivity.getStartTime(), foundActivity.getEndTime());
          foundActivity.setDuration(duration);
        }
        this.port.update(foundActivity);
        return;
      }
      ActivityEntity activity = new ActivityEntity(0, event.getApp(), event.getTitle(), event.getUrl(),
          event.getStartTime(), event.getEndTime(), event.getDuration());
      activity.setDuration(null);
      this.port.create(activity);
    } catch (SQLException e) {
      this.logManager.error("SQL Error during UpsertActivityEventHandler.handle:%s".formatted(e.getMessage()));
      e.printStackTrace();
    } catch (Exception e) {
      this.logManager.error("Unexpected error during UpsertActivityEventHandler.handle:%s".formatted(e.getMessage()));
      e.printStackTrace();
    }
    return;
  }
}
