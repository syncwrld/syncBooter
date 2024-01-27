package me.syncwrld.booter.application;

import me.syncwrld.booter.ApplicationType;

public interface AppConfiguration {
  public ApplicationType appType();

  public String appName();

  public String appDescription();

  public String appVersion();
}
