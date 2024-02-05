package me.syncwrld.booter.database;

public class Value {

  private Type type;
  private Object value;

  public static Value of(Type type, Object value) {
    Value v = new Value();
    v.type = type;
    v.value = value;
    return v;
  }

  @SuppressWarnings("unchecked")
  public <T> T getValue() {
    return (T) value;
  }

  public static enum Type {
    STRING,
    INTEGER,
    DOUBLE,
    JSON,
    BIGINT,
    LONG
  }
}
