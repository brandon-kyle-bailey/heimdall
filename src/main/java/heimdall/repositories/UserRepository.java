
package heimdall.repositories;

import java.sql.SQLException;
import java.util.List;

import heimdall.adapters.PersistenceAdapter;

public class UserRepository extends PersistenceAdapter<UserDto> {
  private final String _schema = "user";

  public UserRepository() {
    try {
      this.migrate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void migrate() throws SQLException {
    String createTableSQL = "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, userId TEXT NOT NULL UNIQUE, accountId TEXT NOT NULL UNIQUE)"
        .formatted(this._schema);
    createTableIfNotExists(createTableSQL);
  }

  // Create User
  public int create(UserDto user) throws SQLException {
    String sql = "INSERT INTO %s (userId, accountId) VALUES (?, ?)".formatted(this._schema);
    return create(sql, user.getUserId(), user.getAccountId());
  }

  // Read User by ID
  public UserDto read(int id) throws SQLException {
    String sql = "SELECT * FROM %s WHERE id = ?".formatted(this._schema);
    return read(sql, UserDtoMapper::toDomain, id);
  }

  // Method to find users by userId
  public List<UserDto> findUsersByAccountIdAndUserId(String accountId, String userId) throws SQLException {
    String sql = "SELECT * FROM %s WHERE accountId = ? AND userId = ?".formatted(this._schema);
    return list(sql, UserDtoMapper::toDomain, accountId, userId);
  }

  // List all users
  public List<UserDto> list() throws SQLException {
    String sql = "SELECT * FROM %s".formatted(this._schema);
    return list(sql, UserDtoMapper::toDomain);
  }

  // Update User
  public int update(UserDto user) throws SQLException {
    String sql = "UPDATE %s SET accountId = ?, userId = ? WHERE id = ?".formatted(this._schema);
    return update(sql, user.getAccountId(), user.getUserId(), user.getId());
  }

  // Delete User by ID
  public int delete(int id) throws SQLException {
    String sql = "DELETE FROM %s WHERE id = ?".formatted(this._schema);
    return delete(sql, id);
  }
}
