package heimdall.dtos.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import heimdall.dtos.AppDto;
import heimdall.common.interfaces.IDtoMapper;

public class AppDtoMapper implements IDtoMapper<AppDto> {

  public static AppDto persistenceToDomain(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String name = resultSet.getString("name");
    return new AppDto(id, name);
  }

  public static AppDto interfaceToDomain(JSONObject input) {
    int id = input.getInt("id");
    String name = input.getString("name");
    return new AppDto(id, name);
  }
}
