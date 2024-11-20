package heimdall.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import heimdall.repositories.models.ApplicationModel;

public class ApplicationModelMapper {

  public static ApplicationModel toDomain(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String name = resultSet.getString("name");
    return new ApplicationModel(id, name);
  }
}
