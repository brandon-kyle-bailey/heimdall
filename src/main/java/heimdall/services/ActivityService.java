package heimdall.services;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.json.JSONObject;

import heimdall.entities.ActivityEntity;
import heimdall.ports.ActivityPort;
import heimdall.ports.LoggingPort;

public class ActivityService {
  LoggingPort logManager;
  ActivityPort port;

  public ActivityService(LoggingPort logManager, ActivityPort port) {
    this.logManager = logManager;
    this.port = port;
  }

  public void upsert(JSONObject event) {
    this.logManager.debug("ActivityService.upsert invoked: %s".formatted(event.toString()));
    try {
      List<ActivityEntity> foundActivities = this.port.findLatestByNameTitleUrl(
          event.getString("name"), event.getString("title"), event.getString("url"));
      ActivityEntity foundActivity = foundActivities.isEmpty() ? null : foundActivities.get(0);
      if (foundActivity != null && event.has("endTime")) {
        LocalDateTime startTime = foundActivity.getStartTime();
        LocalDateTime endTime = LocalDateTime.parse(event.getString("endTime"));
        System.out
            .println("found activity start and end time: %s %s".formatted(startTime.toString(), endTime.toString()));
        // Calculate the duration in milliseconds
        Integer durationMillis = (int) ChronoUnit.MILLIS.between(startTime, endTime);
        foundActivity.setDuration(durationMillis);
        foundActivity.setEndTime(endTime);
        this.logManager.debug("Found activity. Updating duration: %s".formatted(event.toString()));
        this.port.update(foundActivity);
        return;
      }
      this.logManager.debug("No activity found. Creating activity: %s".formatted(event.toString()));
      LocalDateTime startTime = LocalDateTime.parse(event.getString("startTime"));
      LocalDateTime endTime = event.isNull("endTime") ? null : LocalDateTime.parse(event.getString("endTime"));
      Integer duration = event.isNull("duration") ? null : event.getInt("duration");
      ActivityEntity activity = new ActivityEntity(
          0,
          event.getString("name"),
          event.getString("title"),
          event.getString("url"),
          startTime,
          endTime,
          duration);
      this.port.create(activity);
      // HttpService.post(activity);
    } catch (SQLException e) {
      this.logManager.error("SQL Error during UpsertActivityEventHandler.handle:%s".formatted(e.getMessage()));
    } catch (Exception e) {
      this.logManager.error("Unexpected error during UpsertActivityEventHandler.handle:%s".formatted(e.getMessage()));
    }
    return;
  }
}
