package me.syncwrld.booter.database;

import me.syncwrld.booter.database.util.Async;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

}
