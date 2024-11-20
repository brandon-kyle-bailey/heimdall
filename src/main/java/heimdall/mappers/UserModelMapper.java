package heimdall.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import heimdall.repositories.models.UserModel;

public class UserModelMapper {

  public static UserModel toDomain(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String accountId = resultSet.getString("accountId");
    String userId = resultSet.getString("userId");
    return new UserModel(id, userId, accountId);
  }
}
