package heimdall.repositories;

import java.sql.SQLException;
import java.util.List;

import heimdall.mappers.ApplicationModelMapper;
import heimdall.repositories.models.ApplicationModel;

public class ApplicationPersistenceRepository extends PersistenceRepository<ApplicationModel> {
  private final String _schema = "applications";

  public ApplicationPersistenceRepository() {
    try {
      this.createApplicationTableIfNotExists();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void createApplicationTableIfNotExists() throws SQLException {
    String createTableSQL = "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE)"
        .formatted(this._schema);
    createTableIfNotExists(createTableSQL);
  }

  public int create(ApplicationModel model) throws SQLException {
    String sql = "INSERT INTO %s (name) VALUES (?)".formatted(this._schema);
    return create(sql, model.getName());
  }

  public ApplicationModel read(int id) throws SQLException {
    String sql = "SELECT * FROM %s WHERE id = ?".formatted(this._schema);
    return read(sql, ApplicationModelMapper::toDomain, id);
  }

  public List<ApplicationModel> list() throws SQLException {
    String sql = "SELECT * FROM %s".formatted(this._schema);
    return list(sql, ApplicationModelMapper::toDomain);
  }

  public int update(ApplicationModel model) throws SQLException {
    String sql = "UPDATE %s SET name = ? WHERE id = ?".formatted(this._schema);
    return update(sql, model.getName(), model.getId());
  }

  public int delete(int id) throws SQLException {
    String sql = "DELETE FROM %s WHERE id = ?".formatted(this._schema);
    return delete(sql, id);
  }

  public List<ApplicationModel> findByName(String name) throws SQLException {
    String sql = "SELECT * FROM %s WHERE name = ?".formatted(this._schema);
    return list(sql, ApplicationModelMapper::toDomain, name);
  }
}
