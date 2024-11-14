package me.syncwrld.booter.database.connector;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.AccessLevel;
import lombok.Getter;
import me.syncwrld.booter.annotation.IncludeOnGetter;
import me.syncwrld.booter.database.BukkitDatabaseCredentials;
import me.syncwrld.booter.database.DatabaseCredentials;
import me.syncwrld.booter.database.connector.sample.DatabaseType;

@Getter(value = AccessLevel.PUBLIC)
public class SimpleDatabaseConnector {

  private final DatabaseType databaseType;
  private Connection connection;

  private File sqliteFile;
  private DatabaseCredentials databaseCredentials;

  public SimpleDatabaseConnector(DatabaseType databaseType, File sqliteFile) {
    this.databaseType = databaseType;
    this.sqliteFile = sqliteFile;
  }

  public SimpleDatabaseConnector(
      DatabaseType databaseType, DatabaseCredentials databaseCredentials) {
    this.databaseType = databaseType;
    this.databaseCredentials = databaseCredentials;
  }

  public String getDatabaseType() {
    return databaseType.getName();
  }

  public boolean connect() {
    switch (databaseType) {
      case MYSQL_HIKARICP:
        return connectMySQLHikari();
      case MYSQL:
        return connectMySQL();
      case SQLITE:
        return connectSQLite();
      default:
        throw new IllegalStateException("Unexpected value: " + databaseType);
    }
  }

  private boolean connectMySQLHikari() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(
        "jdbc:mysql://"
            + databaseCredentials.getHost()
            + "/"
            + databaseCredentials.getDatabase()
            + "?autoReconnect=true");
    config.setUsername(databaseCredentials.getUsername());
    config.setPassword(databaseCredentials.getPassword());
    config.setAutoCommit(false);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    HikariDataSource dataSource = new HikariDataSource(config);
    try {
      connection = dataSource.getConnection();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private boolean connectSQLite() {
    Preconditions.checkNotNull(sqliteFile);

    if (!sqliteFile.exists()) {
      sqliteFile.getParentFile().mkdirs();
        try {
            sqliteFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    try {
      Class.forName("org.sqlite.JDBC");
      this.connection =
          DriverManager.getConnection("jdbc:sqlite:" + this.sqliteFile.getAbsolutePath());
      return true;
    } catch (SQLException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean connectMySQL() {
    try {
      this.connection =
          DriverManager.getConnection(
              "jdbc:mysql://"
                  + this.databaseCredentials.getHost()
                  + "/"
                  + this.databaseCredentials.getDatabase()
                  + "?autoReconnect=true",
              this.databaseCredentials.getUsername(),
              this.databaseCredentials.getPassword());
      return true;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
