package me.syncwrld.booter.application;

import java.util.concurrent.ThreadLocalRandom;
import me.syncwrld.booter.ApplicationType;

public class DefaultAppConfiguration implements AppConfiguration {

  @Override
  public ApplicationType appType() {
    return ApplicationType.GENERAL_JAVA;
  }

  @Override
  public String appName() {
    return "booter-app-" + System.currentTimeMillis();
  }

  @Override
  public String appDescription() {
    return "an incredible syncBooter-based application";
  }

  @Override
  public String appVersion() {
    return ThreadLocalRandom.current().nextInt(0, 9)
        + "."
        + ThreadLocalRandom.current().nextInt(0, 99);
  }
}
