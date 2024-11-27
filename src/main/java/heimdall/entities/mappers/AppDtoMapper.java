package heimdall.entities.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import heimdall.common.interfaces.IDtoMapper;
import heimdall.entities.AppEntity;

public class AppDtoMapper implements IDtoMapper<AppEntity> {

  public static AppEntity persistenceToDomain(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String name = resultSet.getString("name");
    return new AppEntity(id, name);
  }

  public static AppEntity interfaceToDomain(JSONObject input) {
    int id = input.getInt("id");
    String name = input.getString("name");
    return new AppEntity(id, name);
  }
}
