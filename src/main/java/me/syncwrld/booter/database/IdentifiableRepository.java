package me.syncwrld.booter.database;

import java.util.Map;

public interface IdentifiableRepository {
  String getName();

  boolean hasMoreThanOneTable();

  void createTables();

  Map<String, Integer> getTableIDs();

  default String consolidate(String query) {
    for (Map.Entry<String, Integer> entry : getTableIDs().entrySet()) {
      String placeholder = "$table_" + entry.getValue();
      String tableName = entry.getKey();
      query = query.replaceAll(placeholder, tableName);
    }
    return query;
  }
}
