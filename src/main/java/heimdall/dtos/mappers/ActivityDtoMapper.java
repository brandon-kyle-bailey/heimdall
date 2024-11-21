package heimdall.dtos.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.json.JSONObject;

import heimdall.dtos.ActivityDto;
import heimdall.common.interfaces.IDtoMapper;

public class ActivityDtoMapper implements IDtoMapper<ActivityDto> {

  public static ActivityDto persistenceToDomain(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String appName = resultSet.getString("appName");
    String title = resultSet.getString("title");
    String url = resultSet.getString("url");
    LocalDateTime startTime = LocalDateTime.parse(resultSet.getString("startTime"));
    LocalDateTime endTime = LocalDateTime.parse(resultSet.getString("endTime"));
    Integer duration = resultSet.getInt("duration");
    return new ActivityDto(id, appName, title, url, startTime, endTime, duration);
  }

  public static ActivityDto interfaceToDomain(JSONObject input) {
    int id = input.getInt("id");
    String appName = input.getString("appName");
    String title = input.getString("title");
    String url = input.getString("url");
    LocalDateTime startTime = LocalDateTime.parse(input.getString("startTime"));
    LocalDateTime endTime = LocalDateTime.parse(input.getString("endTime"));
    Integer duration = input.getInt("duration");
    return new ActivityDto(id, appName, title, url, startTime, endTime, duration);
  }
}
