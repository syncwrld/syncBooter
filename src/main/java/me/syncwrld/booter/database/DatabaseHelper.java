package me.syncwrld.booter.database;

import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import me.syncwrld.booter.database.util.Async;

public interface DatabaseHelper {

  default <T> T async(Callable<T> callable) {
    return Async.run(callable).join();
  }

  default Future<?> sync(Runnable runnable) {
    return Executors.newCachedThreadPool().submit(runnable);
  }

  default PreparedStatement prepare(Connection connection, String query) {
    return Async.run(
            () -> {
              try {
                return connection.prepareStatement(query);
              } catch (SQLException e) {
                throw new RuntimeException(e);
              }
            })
        .join();
  }

  default ResultSet result(PreparedStatement prepared) {
    return Async.run(
            () -> {
              try {
                return prepared.executeQuery();
              } catch (SQLException e) {
                throw new RuntimeException(e);
              }
            })
        .join();
  }

  default void insert(String tableId, Connection connection, String... values) {
    int valuesLength = values.length;

    StringBuilder query = new StringBuilder();
    query.append("insert into ").append(tableId).append(" values (");
    for (int i = 0; i < valuesLength; i++) {
      query.append("?");
      if (i != valuesLength - 1) {
        query.append(", ");
      }
    }
    query.append(")");

    PreparedStatement prepared = prepare(connection, query.toString());
    try {
      for (int i = 0; i < valuesLength; i++) {
        prepared.setString(i + 1, values[i]);
      }
      prepared.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  default <T> T get(
      int tableId, Connection connection, String primaryKey, String key, Class<T> type) {
    PreparedStatement prepare =
        this.prepare(connection, "select * from " + tableId + " where " + primaryKey + " = ?");

    try {
      prepare.setString(1, key);
      ResultSet result = prepare.executeQuery();
      if (result.next()) {
        return type.cast(result.getObject(1));
      } else {
        return null;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  default <T> ArrayList<T> select(
      String tableId, Connection connection, String condition, Class<T> type) {
    PreparedStatement prepare =
        this.prepare(connection, "select * from " + tableId + " where " + condition);

    try {
      ResultSet result = prepare.executeQuery();
      ArrayList<T> list = new ArrayList<>();
      while (result.next()) {
        list.add(type.cast(result.getObject(1)));
      }
      return list;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  default void replace(
      String tableId, Connection connection, int valuesLength, String where, String... values) {
    StringBuilder query = new StringBuilder();

    query.append("replace into ").append(tableId).append(" values (");
    for (int i = 0; i < valuesLength; i++) {
      query.append("?");
      if (i != valuesLength - 1) {
        query.append(", ");
      }
    }
    query.append(")");
    query.append(" where ").append(where);

    PreparedStatement prepared = prepare(connection, query.toString());
    try {
      for (int i = 0; i < valuesLength; i++) {
        prepared.setString(i + 1, values[i]);
      }
      prepared.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  default boolean createTable(Connection connection, String name, TableComponent... components) {
    Preconditions.checkNotNull(connection, "Connection cannot be null");

    StringBuilder componentQuery = new StringBuilder();
    boolean hasPrimaryKey = false;
    componentQuery.append(" (");

    for (int i = 0; i < components.length; i++) {
      TableComponent component = components[i];

      String identifier = component.getIdentifier();
      String correspondent = component.getType().getCorrespondent();

      componentQuery.append(identifier).append(" ").append(correspondent);

      if (component.isPrimaryKey() && !hasPrimaryKey) {
        hasPrimaryKey = true;
        componentQuery.append(" primary key");
      }

      if (i == components.length - 1) {
        componentQuery.append(")");
      } else {
        componentQuery.append(", ");
      }
    }

    String query = "create table if not exists " + name + componentQuery.toString();

    try {
      PreparedStatement prepared = connection.prepareStatement(query);
      return prepared.executeUpdate() != 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public default void clearValues(Connection connection, int tableID) {
    if (!(this instanceof IdentifiableRepository)) {
      throw new RuntimeException(
          "Class must be a IdentifiableRepository to be able to clear values programmatically.");
    }

    IdentifiableRepository identifiableRepository = (IdentifiableRepository) this;
    Map<String, Integer> tableIDs = identifiableRepository.getTableIDs();
    Map.Entry<String, Integer> tableEntry =
        tableIDs.entrySet().stream().filter(e -> e.getValue() == tableID).findFirst().orElse(null);

    if (tableEntry != null) {
      String table = tableEntry.getKey();
      String query = "truncate table " + table;
      PreparedStatement prepared = prepare(connection, query);
      try {
        prepared.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
