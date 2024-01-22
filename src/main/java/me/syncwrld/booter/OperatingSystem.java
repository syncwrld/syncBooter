package me.syncwrld.booter;

import lombok.Getter;

@Getter
public enum OperatingSystem {
  WINDOWS("Windows", "10"),
  LINUX("Linux", "4"),
  MAC("Mac", "10"),
  UNKNOWN("Unknown", "0");

  private final String name;
  private final String mostUsedVersion;

  OperatingSystem(String name, String mostUsedVersion) {
    this.name = name;
    this.mostUsedVersion = mostUsedVersion;
  }
}
