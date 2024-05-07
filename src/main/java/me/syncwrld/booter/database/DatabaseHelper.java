package me.syncwrld.booter.database;

import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
              try (PreparedStatement prepared = connection.prepareStatement(query)) {
                return prepared;
              } catch (SQLException e) {
                throw new RuntimeException(e);
              }
            })
        .join();
  }

  default ResultSet result(PreparedStatement prepared) {
    return Async.run(
            () -> {
              try (ResultSet resultSet = prepared.executeQuery()) {
                return resultSet;
              } catch (SQLException e) {
                throw new RuntimeException(e);
              }
            })
        .join();
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
    System.out.println(query);
    PreparedStatement prepared = prepare(connection, query);

    try {
      return prepared.executeUpdate() != 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public default void clearValues(Connection connection, int tableID) {
    if (!(this instanceof IdentifiableRepository)) {
      throw new RuntimeException("Class must be a IdentifiableRepository to be able to clear values programmatically.");
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
