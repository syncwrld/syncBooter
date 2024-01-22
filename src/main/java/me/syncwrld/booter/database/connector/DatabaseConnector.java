package me.syncwrld.booter.database.connector;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.SneakyThrows;
import lombok.experimental.Tolerate;
import me.syncwrld.booter.common.tool.JsonParser;

public class DatabaseConnector {

  private final String databaseType;
  private Connection connection;
  private String file;

  private String host;
  private String database;
  private String username;
  private String password;
  private String errorMessage = "Connection Failed";

  public DatabaseConnector(String databaseType, String file) {
    this.databaseType = databaseType;
    this.file = file;
  }

  public DatabaseConnector(
      String databaseType, String host, String database, String username, String password) {
    this.databaseType = databaseType;
    this.host = host;
    this.database = database;
    this.username = username;
    this.password = password;
  }

  @SneakyThrows
  public static DatabaseConnector withConfiguration(File configuration) {
    String parserType = "";
    try {
      Class.forName("org.bukkit.configuration.file.YamlConfiguration");
      parserType = "bukkitYaml";
    } catch (ClassNotFoundException e) {
      parserType = "commonJson";
    }

    String type, host, database, username, password;


    if (parserType.equals("bukkitYaml")) {
      org.bukkit.configuration.file.FileConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(configuration);
      type = config.getString("database.type");
      host = config.getString("database.host");
      database = config.getString("database.database");
      username = config.getString("database.username");
      password = config.getString("database.password");
    } else {
      JsonObject config = new JsonParser().getObject(configuration);
      type = config.get("database.type").getAsString();
      host = config.get("database.host").getAsString();
      database = config.get("database.database").getAsString();
      username = config.get("database.username").getAsString();
      password = config.get("database.password").getAsString();
    }

    return new DatabaseConnector(type, host, database, username, password);
  }

  public boolean connect() {
    if ("mysql".equalsIgnoreCase(databaseType)) {
      try {
        Preconditions.checkNotNull(host, "Database Host can't be null");
        Preconditions.checkNotNull(database, "Database Name can't be null");
        Preconditions.checkNotNull(username, "Database Username can't be null");
        Preconditions.checkNotNull(password, "Database Password can't be null");

        this.connection =
            DriverManager.getConnection(
                "jdbc:mysql://" + host + "/" + database + "?autoReconnect=true",
                username,
                password);
        return true;
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    } else {
      try {
        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + file);
        return true;
      } catch (SQLException | ClassNotFoundException e) {
        throw new RuntimeException(errorMessage, e);
      }
    }
  }

  public boolean connect(String errorMessage) {
    this.errorMessage = errorMessage;
    return connect();
  }

  public void disconnect() {
    if (isConnected()) {
      try {
        connection.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Tolerate
  public Connection getConnection() {
    return connection;
  }

  public boolean isConnected() {
    return connection != null;
  }

  public String getDatabaseType() {
    return databaseType;
  }

  public String getFormattedType() {
    return databaseType.equals("sqlite") ? "SQLite" : "MySQL";
  }
}
