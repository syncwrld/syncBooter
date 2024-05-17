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
}
