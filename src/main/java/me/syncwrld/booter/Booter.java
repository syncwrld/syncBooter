package me.syncwrld.booter;

import java.util.logging.*;

public class Booter {
  private static final Logger logger = Logger.getLogger("me.syncwrld.booter.Booter");

  public static Logger getLogger() {
    for (Handler handler : logger.getParent().getHandlers()) {
      logger.getParent().removeHandler(handler);
    }

    Handler handler = new ConsoleHandler();
    Formatter formatter =
        new Formatter() {
          @Override
          public String format(LogRecord record) {
            return String.format(
                "[%s] [%s] %s\n", Constants.NAME, record.getLevel(), record.getMessage());
          }
        };
    handler.setFormatter(formatter);
    logger.getParent().addHandler(handler);

    return logger;
  }
}
