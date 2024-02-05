package me.syncwrld.booter.database;

import lombok.Data;

@Data
public class TableComponent {

  private final Type type;
  private final String identifier;
  private final boolean primaryKey;
  private final boolean notNull;

  public TableComponent(Type type, String identifier, boolean primaryKey, boolean notNull) {
    this.type = type;
    this.identifier = identifier;
    this.primaryKey = primaryKey;
    this.notNull = notNull;
  }

  public TableComponent(Type type, String identifier, boolean primaryKey) {
    this.type = type;
    this.identifier = identifier;
    this.primaryKey = primaryKey;
    this.notNull = false;
  }

  public TableComponent(Type type, String identifier) {
    this(type, identifier, false, true);
  }

  public static enum Type {
    TEXT("TEXT"),
    JSON("JSON"),
    INT("INT"),
    BOOLEAN("BOOLEAN"),
    DOUBLE("DOUBLE"),
    FLOAT("FLOAT"),
    BIGINT("BIGINT"),
    TINYINT("TINYINT"),
    DATE("DATE"),
    VARCHAR_255("VARCHAR(255)"),
    VARCHAR_128("VARCHAR(128)"),
    VARCHAR_64("VARCHAR(64)"),
    VARCHAR_32("VARCHAR(32)"),
    VARCHAR_16("VARCHAR(16)"),
    VARCHAR_12("VARCHAR(12)");

    private final String correspondent;

    Type(String correspondent) {
      this.correspondent = correspondent;
    }

    public String getCorrespondent() {
      return correspondent;
    }
  }
}
