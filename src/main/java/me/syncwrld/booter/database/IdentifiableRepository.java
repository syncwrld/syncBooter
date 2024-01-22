package me.syncwrld.booter.database;

import java.util.Map;

public interface IdentifiableRepository {
  String getName();
  boolean hasMoreThanOneTable();
  void createTables();
  Map<String, Integer> getTableIDs();

  default String consolidate(String query) {
    String consolidate = query;
    for (Map.Entry<String, Integer> entry : getTableIDs().entrySet()) {
      String placeholder = "$table_" + entry.getValue();
      String tabelaName = entry.getKey();
      consolidate = consolidate.replaceAll(placeholder, tabelaName);
    }
    return consolidate;
  }
}
