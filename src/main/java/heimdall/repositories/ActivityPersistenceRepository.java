package heimdall.repositories;

import java.sql.SQLException;
import java.util.List;

import heimdall.mappers.ActivityModelMapper;
import heimdall.repositories.models.ActivityModel;

public class ActivityPersistenceRepository extends PersistenceRepository<ActivityModel> {
  private final String _schema = "activities";

  public ActivityPersistenceRepository() {
    try {
      this.createActivityTableIfNotExists();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void createActivityTableIfNotExists() throws SQLException {
    String createTableSQL = "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, appName TEXT NOT NULL, title TEXT NOT NULL, url TEXT NULL, startTime TEXT NOT NULL, endTime TEXT NULL, duration int NULL)"
        .formatted(this._schema);
    createTableIfNotExists(createTableSQL);
  }

  public int create(ActivityModel model) throws SQLException {
    String sql = "INSERT INTO %s (appName, title, url, startTime, endTime, duration) VALUES (?, ?, ?, ?, ?, ?)"
        .formatted(this._schema);
    return create(sql, model.getAppName(), model.getTitle(), model.getUrl(), model.getStartTime(), model.getEndTime(),
        model.getDuration());
  }

  public ActivityModel read(int id) throws SQLException {
    String sql = "SELECT * FROM %s WHERE id = ?".formatted(this._schema);
    return read(sql, ActivityModelMapper::toDomain, id);
  }

  public List<ActivityModel> list() throws SQLException {
    String sql = "SELECT * FROM %s".formatted(this._schema);
    return list(sql, ActivityModelMapper::toDomain);
  }

  public int update(ActivityModel model) throws SQLException {
    String sql = "UPDATE %s SET appName = ?, title = ?, url = ?, startTime = ?, endtime = ?, duration = ? WHERE id = ?"
        .formatted(this._schema);
    return update(sql, model.getAppName(), model.getTitle(), model.getUrl(), model.getStartTime(), model.getEndTime(),
        model.getDuration(),
        model.getId());
  }

  public int delete(int id) throws SQLException {
    String sql = "DELETE FROM %s WHERE id = ?".formatted(this._schema);
    return delete(sql, id);
  }

  public List<ActivityModel> findLatestByAppNameTitleUrl(String appName, String title, String url) throws SQLException {
    String sql = "SELECT * FROM %s WHERE appName = ? and title = ? and url = ? and endTime is null ORDER BY id DESC lIMIT 1"
        .formatted(this._schema);
    return list(sql, ActivityModelMapper::toDomain, appName, title, url);
  }
}
