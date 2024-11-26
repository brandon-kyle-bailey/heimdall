package heimdall.infrastructure.adapters.drivers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PersistenceDriverAdapter {
  private static String DB_DRIVER = "jdbc:sqlite:";
  private static String DB_NAME = "p8.db";

  private static String getDatabaseUrl() {
    return PersistenceDriverAdapter.DB_DRIVER + System.getProperty("user.home") + PersistenceDriverAdapter.DB_NAME;
  }

  public static Connection getConnection() throws SQLException {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.err.println("SQLite JDBC driver not found.");
      e.printStackTrace();
    }
    return DriverManager.getConnection(PersistenceDriverAdapter.getDatabaseUrl());
  }
}
