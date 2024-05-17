package me.syncwrld.booter.database.connector;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.io.File;
import me.syncwrld.booter.database.BukkitDatabaseCredentials;
import me.syncwrld.booter.database.connector.sample.DatabaseType;
import me.syncwrld.booter.minecraft.loader.BukkitPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class SimpleBukkitDatabaseConnector {

  public static SimpleDatabaseConnector construct(
      BukkitPlugin bukkitPlugin, FileConfiguration config) {
    Preconditions.checkNotNull(config, "Config cannot be null");
    String rawDatabaseType = config.getString("database.type");

    Preconditions.checkArgument(
        !Strings.isNullOrEmpty(rawDatabaseType), "Database type cannot be null or empty");

    File dataFolder = bukkitPlugin.getDataFolder();
    DatabaseType databaseType = getDatabaseType(rawDatabaseType);

    if (databaseType != null) {
      switch (databaseType) {
        case MYSQL:
        case MYSQL_HIKARICP:
          BukkitDatabaseCredentials bukkitDatabaseCredentials =
              BukkitDatabaseCredentials.withConfiguration(config);
          return new SimpleDatabaseConnector(
              DatabaseType.MYSQL, BukkitDatabaseCredentials.morph(bukkitDatabaseCredentials));
        case SQLITE:
          String sqliteFile = config.getString("database.sqlite-file");
          Preconditions.checkNotNull(
              sqliteFile, "SQLite file cannot be null if SQLite database is selected");
          return new SimpleDatabaseConnector(DatabaseType.SQLITE, new File(dataFolder, sqliteFile));
        default:
          throw new IllegalArgumentException("Invalid database type: " + rawDatabaseType);
      }
    } else {
      throw new IllegalArgumentException("Invalid database type: " + rawDatabaseType);
    }
  }

  public static DatabaseType getDatabaseType(String rawType) {
    if (Strings.isNullOrEmpty(rawType)) {
      return null;
    }

    switch (rawType.toUpperCase()) {
      case "MYSQL":
        return DatabaseType.MYSQL;
      case "MYSQL-HIKARI":
        return DatabaseType.MYSQL_HIKARICP;
      case "SQLITE":
        return DatabaseType.SQLITE;
      default:
        throw new IllegalArgumentException("Invalid database type: " + rawType);
    }
  }
}
