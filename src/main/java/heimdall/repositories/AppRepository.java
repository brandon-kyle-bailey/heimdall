
package heimdall.repositories;

import java.sql.SQLException;
import java.util.List;

import heimdall.adapters.PersistenceAdapter;
import heimdall.dtos.AppDto;
import heimdall.dtos.mappers.AppDtoMapper;

public class AppRepository extends PersistenceAdapter<AppDto> {
  private final String _schema = "app";

  public AppRepository() {
    try {
      this.migrate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void migrate() throws SQLException {
    String createTableSQL = "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE)"
        .formatted(this._schema);
    createTableIfNotExists(createTableSQL);
  }

  // Create User
  public int create(AppDto model) throws SQLException {
    String sql = "INSERT INTO %s (name) VALUES (?)".formatted(this._schema);
    return create(sql, model.getName());
  }

  // Read User by ID
  public AppDto read(int id) throws SQLException {
    String sql = "SELECT * FROM %s WHERE id = ?".formatted(this._schema);
    return read(sql, AppDtoMapper::persistenceToDomain, id);
  }

  // Method to find users by userId
  public List<AppDto> findByName(String name) throws SQLException {
    String sql = "SELECT * FROM %s WHERE name = ?".formatted(this._schema);
    return list(sql, AppDtoMapper::persistenceToDomain, name);
  }

  // List all users
  public List<AppDto> list() throws SQLException {
    String sql = "SELECT * FROM %s".formatted(this._schema);
    return list(sql, AppDtoMapper::persistenceToDomain);
  }

  // Update User
  public int update(AppDto model) throws SQLException {
    String sql = "UPDATE %s SET name = ? WHERE id = ?".formatted(this._schema);
    return update(sql, model.getName(), model.getId());
  }

  // Delete User by ID
  public int delete(int id) throws SQLException {
    String sql = "DELETE FROM %s WHERE id = ?".formatted(this._schema);
    return delete(sql, id);
  }
}
