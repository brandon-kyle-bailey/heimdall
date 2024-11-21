
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDtoMapper {

  public static UserDto toDomain(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String accountId = resultSet.getString("accountId");
    String userId = resultSet.getString("userId");
    return new UserDto(id, userId, accountId);
  }
}
