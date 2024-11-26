package heimdall.infrastructure.mappers.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import heimdall.common.interfaces.IDtoMapper;
import heimdall.core.domain.entities.user.UserEntity;

public class UserDtoMapper implements IDtoMapper<UserEntity> {

  public static UserEntity persistenceToDomain(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String accountId = resultSet.getString("accountId");
    String userId = resultSet.getString("userId");
    return new UserEntity(id, userId, accountId);
  }

  public static UserEntity interfaceToDomain(JSONObject input) {
    int id = input.getInt("id");
    String accountId = input.getString("accountId");
    String userId = input.getString("userId");
    return new UserEntity(id, userId, accountId);
  }
}
