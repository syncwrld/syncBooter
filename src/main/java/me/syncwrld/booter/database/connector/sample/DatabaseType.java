package me.syncwrld.booter.database.connector.sample;

public enum DatabaseType {
  SQLITE("SQLite"),
  MYSQL("MySQL"),
  MYSQL_HIKARICP("MySQL with Hikari");

  private final String name;

  DatabaseType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
