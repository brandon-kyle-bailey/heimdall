package heimdall.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import heimdall.repositories.models.ActivityModel;

public class ActivityModelMapper {

  public static ActivityModel toDomain(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String appName = resultSet.getString("appName");
    String title = resultSet.getString("title");
    String url = resultSet.getString("url");
    LocalDateTime startTime = LocalDateTime.parse(resultSet.getString("startTime"));
    String endTimeString = resultSet.getString("endTime");
    LocalDateTime endTime = null;
    if (endTimeString != null) {
      endTime = LocalDateTime.parse(endTimeString);
    }
    return new ActivityModel(id, appName, title, url, startTime, endTime);
  }
}
