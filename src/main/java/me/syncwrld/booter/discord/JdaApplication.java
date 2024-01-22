package me.syncwrld.booter.discord;

import com.google.common.base.Strings;
import lombok.experimental.Tolerate;
import me.syncwrld.booter.ApplicationBootstrapper;
import me.syncwrld.booter.ApplicationLoader;
import me.syncwrld.booter.discord.extend.BooterJDA;
import me.syncwrld.booter.discord.extend.BooterJDABuilder;

public abstract class JdaApplication extends ApplicationLoader implements ApplicationBootstrapper {

  protected boolean alreadyConfigured = false;
  private String token;
  private String name;
  private BooterJDA jda;

  public void createApp() {
    try {
      name = getName();
      token = getToken();

      jda = BooterJDABuilder.of(this);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public BooterJDA getJDA() {
    return jda;
  }

  @Tolerate
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    if (alreadyConfigured) {
      throw new IllegalStateException(
          "You can't change the token once it's set and BooterJDA is created");
    }

    if (Strings.isNullOrEmpty(token)) {
      throw new IllegalArgumentException(
          "Booter Discord Application token cannot be null or empty");
    }

    this.token = token;
    if (!(Strings.isNullOrEmpty(name))) alreadyConfigured = true;
  }

  @Tolerate
  public String getName() {
    if (name == null && !(alreadyConfigured)) {
      throw new IllegalStateException("You must set the name before creating the BooterJDA");
    }
    return name;
  }

  public void setName(String name) {
    if (alreadyConfigured) {
      throw new IllegalStateException(
          "You can't change the name once it's set and BooterJDA is created");
    }

    if (Strings.isNullOrEmpty(name)) {
      throw new IllegalArgumentException("Booter Discord Application name cannot be null or empty");
    }

    this.name = name;
    if (!(Strings.isNullOrEmpty(token))) alreadyConfigured = true;
  }
}
