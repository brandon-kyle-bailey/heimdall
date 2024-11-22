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
    String name = resultSet.getString("name");
    String title = resultSet.getString("title");
    String url = resultSet.getString("url");

    LocalDateTime startTime = resultSet.getObject("startTime", LocalDateTime.class);
    LocalDateTime endTime = resultSet.getObject("endTime", LocalDateTime.class);

    Integer duration = resultSet.getInt("duration");
    return new ActivityDto(id, name, title, url, startTime, endTime, duration);
  }

  public static ActivityDto interfaceToDomain(JSONObject input) {
    int id = input.getInt("id");
    String name = input.getString("name");
    String title = input.getString("title");
    String url = input.getString("url");

    LocalDateTime startTime = null;
    if (input.has("startTime") && !input.isNull("startTime")) {
      startTime = LocalDateTime.parse(input.getString("startTime"));
    }

    LocalDateTime endTime = null;
    if (input.has("endTime") && !input.isNull("endTime")) {
      endTime = LocalDateTime.parse(input.getString("endTime"));
    }

    Integer duration = null;
    if (input.has("duration") && !input.isNull("duration")) {
      duration = input.getInt("duration");
    }
    return new ActivityDto(id, name, title, url, startTime, endTime, duration);
  }
}
