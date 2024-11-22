package heimdall.repositories;

import java.sql.SQLException;
import java.util.List;

import heimdall.adapters.PersistenceAdapter;
import heimdall.dtos.ActivityDto;
import heimdall.dtos.mappers.ActivityDtoMapper;

public class ActivityRepository extends PersistenceAdapter<ActivityDto> {
  private final String _schema = "activity";

  public ActivityRepository() {
    try {
      this.migrate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void migrate() throws SQLException {
    String createTableSQL = "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, title TEXT NOT NULL, url TEXT NULL, startTime TEXT NOT NULL, endTime TEXT NULL, duration int NULL)"
        .formatted(this._schema);
    createTableIfNotExists(createTableSQL);
  }

  public int create(ActivityDto model) throws SQLException {
    String sql = "INSERT INTO %s (name, title, url, startTime, endTime, duration) VALUES (?, ?, ?, ?, ?, ?)"
        .formatted(this._schema);
    return create(sql, model.getName(), model.getTitle(), model.getUrl(), model.getStartTime(), model.getEndTime(),
        model.getDuration());
  }

  public ActivityDto read(int id) throws SQLException {
    String sql = "SELECT * FROM %s WHERE id = ?".formatted(this._schema);
    return read(sql, ActivityDtoMapper::persistenceToDomain, id);
  }

  public List<ActivityDto> list() throws SQLException {
    String sql = "SELECT * FROM %s".formatted(this._schema);
    return list(sql, ActivityDtoMapper::persistenceToDomain);
  }

  public int update(ActivityDto model) throws SQLException {
    String sql = "UPDATE %s SET name = ?, title = ?, url = ?, startTime = ?, endtime = ?, duration = ? WHERE id = ?"
        .formatted(this._schema);
    return update(sql, model.getName(), model.getTitle(), model.getUrl(), model.getStartTime(), model.getEndTime(),
        model.getDuration(),
        model.getId());
  }

  public int delete(int id) throws SQLException {
    String sql = "DELETE FROM %s WHERE id = ?".formatted(this._schema);
    return delete(sql, id);
  }

  public List<ActivityDto> findLatestByNameTitleUrl(String name, String title, String url) throws SQLException {
    String sql = "SELECT * FROM %s WHERE name = ? and title = ? and url = ? and endTime is null ORDER BY id DESC lIMIT 1"
        .formatted(this._schema);
    return list(sql, ActivityDtoMapper::persistenceToDomain, name, title, url);
  }
}
