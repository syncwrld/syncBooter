package me.syncwrld.booter.discord.extend;

import me.syncwrld.booter.discord.JdaApplication;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public abstract class BooterJDABuilder {

  private final JdaApplication app;

  private BooterJDABuilder(JdaApplication app) {
    this.app = app;
  }

  public static BooterJDA of(JdaApplication app) {
    JDABuilder jdaBuilder =
        JDABuilder.createDefault(app.getToken())
            .enableIntents(
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.MESSAGE_CONTENT);
    JDA jda = jdaBuilder.build();

    return new BooterJDA(jda, jdaBuilder);
  }

  public static BooterJDA of(JdaApplication app, Object readyListener) {
    JDABuilder jdaBuilder =
        JDABuilder.createDefault(app.getToken())
            .addEventListeners(readyListener)
            .enableIntents(
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.MESSAGE_CONTENT);
    JDA jda = jdaBuilder.build();

    return new BooterJDA(jda, jdaBuilder);
  }
}
