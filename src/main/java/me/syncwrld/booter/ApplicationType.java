package me.syncwrld.booter;

import lombok.Getter;

@Getter
public enum ApplicationType {
  JDA_APP("JDA"),
  D4J_APP("Discord4J"),
  JAVACORD_APP("Javacord"),
  GENERAL_JAVA("Java"),
  BUKKIT_PLUGIN("Spigot"),
  VELOCITY_PLUGIN("Velocity"),
  BUNGEECORD_PLUGIN("Bungeecord");

  private final String beautifulName;

  ApplicationType(String beautifulName) {
    this.beautifulName = beautifulName;
  }
}
