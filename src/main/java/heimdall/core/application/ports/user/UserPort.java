package heimdall.core.application.ports.user;

import java.sql.SQLException;
import java.util.List;

import heimdall.infrastructure.adapters.persistence.PersistenceAdapter;
import heimdall.infrastructure.mappers.user.UserDtoMapper;
import heimdall.core.domain.entities.user.UserEntity;

public class UserPort extends PersistenceAdapter<UserEntity> {
  private final String _schema = "user";

  public UserPort() {
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
  public int create(UserEntity user) throws SQLException {
    String sql = "INSERT INTO %s (userId, accountId) VALUES (?, ?)".formatted(this._schema);
    return create(sql, user.getUserId(), user.getAccountId());
  }

  // Read User by ID
  public UserEntity read(int id) throws SQLException {
    String sql = "SELECT * FROM %s WHERE id = ?".formatted(this._schema);
    return read(sql, UserDtoMapper::persistenceToDomain, id);
  }

  // Method to find users by userId
  public List<UserEntity> findUsersByAccountIdAndUserId(String accountId, String userId) throws SQLException {
    String sql = "SELECT * FROM %s WHERE accountId = ? AND userId = ?".formatted(this._schema);
    return list(sql, UserDtoMapper::persistenceToDomain, accountId, userId);
  }

  // List all users
  public List<UserEntity> list() throws SQLException {
    String sql = "SELECT * FROM %s".formatted(this._schema);
    return list(sql, UserDtoMapper::persistenceToDomain);
  }

  // Update User
  public int update(UserEntity user) throws SQLException {
    String sql = "UPDATE %s SET accountId = ?, userId = ? WHERE id = ?".formatted(this._schema);
    return update(sql, user.getAccountId(), user.getUserId(), user.getId());
  }

  // Delete User by ID
  public int delete(int id) throws SQLException {
    String sql = "DELETE FROM %s WHERE id = ?".formatted(this._schema);
    return delete(sql, id);
  }
}
