package heimdall.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class PersistenceRepository<T> {
  private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.home") + "/p8.db";

  protected Connection getConnection() throws SQLException {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.err.println("SQLite JDBC driver not found.");
      e.printStackTrace();
    }
    return DriverManager.getConnection(DB_URL);
  }

  public void createTableIfNotExists(String createTableSQL) throws SQLException {
    try (Connection connection = getConnection();
        Statement statement = connection.createStatement()) {
      statement.execute(createTableSQL);
    }
  }

  private void setParameters(PreparedStatement statement, Object... params) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      statement.setObject(i + 1, params[i]);
    }
  }

  public interface ResultSetMapper<T> {
    T map(ResultSet resultSet) throws SQLException;
  }

  public int create(String sql, Object... params) throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      setParameters(statement, params);
      return statement.executeUpdate();
    }
  }

  public T read(String sql, ResultSetMapper<T> mapper, Object... params) throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      setParameters(statement, params);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapper.map(resultSet);
        } else {
          return null;
        }
      }
    }
  }

  public List<T> list(String sql, ResultSetMapper<T> mapper, Object... params) throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      setParameters(statement, params);
      try (ResultSet resultSet = statement.executeQuery()) {
        List<T> entities = new ArrayList<>();
        while (resultSet.next()) {
          entities.add(mapper.map(resultSet));
        }
        return entities;
      }
    }
  }

  public int update(String sql, Object... params) throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      setParameters(statement, params);
      return statement.executeUpdate();
    }
  }

  public int delete(String sql, Object... params) throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      setParameters(statement, params);
      return statement.executeUpdate();
    }
  }
}
