package me.syncwrld.booter.database;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

@Data
public class DatabaseCredentials {
  private final String username;
  private final String password;
  private final String host;
  private final String database;

  public static DatabaseCredentials withConfiguration(FileConfiguration configuration) {
    final ConfigurationSection databaseSection = configuration.getConfigurationSection("database");
    return new DatabaseCredentials(
        databaseSection.getString("username"),
        databaseSection.getString("password"),
        databaseSection.getString("host"),
        databaseSection.getString("database"));
  }
}
