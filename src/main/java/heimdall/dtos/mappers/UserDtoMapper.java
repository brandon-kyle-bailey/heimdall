package heimdall.dtos.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import heimdall.dtos.UserDto;
import heimdall.common.interfaces.IDtoMapper;

public class UserDtoMapper implements IDtoMapper<UserDto> {

  public static UserDto persistenceToDomain(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String accountId = resultSet.getString("accountId");
    String userId = resultSet.getString("userId");
    return new UserDto(id, userId, accountId);
  }

  public static UserDto interfaceToDomain(JSONObject input) {
    int id = input.getInt("id");
    String accountId = input.getString("accountId");
    String userId = input.getString("userId");
    return new UserDto(id, userId, accountId);
  }
}
