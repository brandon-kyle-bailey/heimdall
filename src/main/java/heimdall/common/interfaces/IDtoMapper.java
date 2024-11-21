package heimdall.common.interfaces;

import java.sql.ResultSet;

import org.json.JSONObject;

public interface IDtoMapper<T> {
  static <T> T persistenceToDomain(ResultSet resultSet) {
    System.err.println("Method not implemented");
    return null;
  }

  static <T> T interfaceToDomain(JSONObject input) {
    System.err.println("Method not implemented");
    return null;
  }
}
