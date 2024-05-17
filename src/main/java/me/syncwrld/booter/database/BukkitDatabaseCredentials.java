package me.syncwrld.booter.database;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

@Data
public class BukkitDatabaseCredentials {
  private final String username;
  private final String password;
  private final String host;
  private final String database;

  /*
  Example configuration:
  # Database #
  database:
    type: 'SQLite' # MySQL or SQLite
    # SQLite #
    sqlite-file: 'storage/database.db'
    # MySQL #
   host: 'localhost'
   database: 's1_booter'
   username: 'root'
   password: ''
   */

  public static BukkitDatabaseCredentials withConfiguration(FileConfiguration configuration) {
    final ConfigurationSection databaseSection = configuration.getConfigurationSection("database");
    return new BukkitDatabaseCredentials(
        databaseSection.getString("username"),
        databaseSection.getString("password"),
        databaseSection.getString("host"),
        databaseSection.getString("database"));
  }

  public static DatabaseCredentials morph(BukkitDatabaseCredentials credentials) {
    return new DatabaseCredentials(
        credentials.getUsername(),
        credentials.getPassword(),
        credentials.getHost(),
        credentials.getDatabase());
  }
}
