package me.syncwrld.booter;

import com.google.common.base.Stopwatch;
import lombok.Getter;

@Getter
public abstract class ApplicationLoader extends CommonPurposeClass {

  private static ApplicationBootstrapper bootstrapper;
  private static Stopwatch stopwatch;

  public static void load() {
    if (bootstrapper == null) {
      throw new IllegalStateException("Application bootstrapper is not set");
    }

    bootstrapper.enable();
    stopwatch.createStarted();
  }

  public static void setBootstrapper(ApplicationBootstrapper bootstrapper) {
    ApplicationLoader.bootstrapper = bootstrapper;
  }

  public void setStopwatch(Stopwatch stopwatch) {
    ApplicationLoader.stopwatch = stopwatch;
  }

  public void shutdown() {
    if (bootstrapper == null) {
      throw new IllegalStateException("Application bootstrapper is not set");
    }

    stopwatch.stop();
    bootstrapper.disable();
  }

  public String getUptime() {
    return stopwatch.toString();
  }
}
