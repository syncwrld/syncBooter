package me.syncwrld.booter;

import java.util.logging.*;
import org.apache.commons.cli.*;

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

  public static void main(String[] args) {
    getLogger().info("Starting " + Constants.NAME);
    Options options = new Options();

    options.addOption("javaapp", true, "Set application type to general java application");
    options.addOption("jdaapp", true, "Set application type to jda application");

    CommandLineParser cmdParser = new DefaultParser();
    CommandLine cmdLine;

    try {
      cmdLine = cmdParser.parse(options, args);

      boolean setJavaApp = cmdLine.hasOption("javaapp");
      boolean setJDAApp = cmdLine.hasOption("jdaapp");

      if (setJavaApp && setJDAApp) {
        getLogger().info("Both application types specified, defaulting to general java application");
        return;
      }

      if (!setJavaApp && !setJDAApp) {
        getLogger().info("No application type specified, defaulting to general java application");
        return;
      }
    } catch (ParseException e) {
      getLogger().info("Error parsing command line: " + e.getMessage());
      return;
    }
  }
}
