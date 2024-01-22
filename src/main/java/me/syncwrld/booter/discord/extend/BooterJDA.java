package me.syncwrld.booter.discord.extend;

import com.google.common.base.Strings;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;

@Getter
public class BooterJDA {

  private final JDA jda;
  private final JDABuilder jdaBuilder;

  public BooterJDA(JDA jda, JDABuilder jdaBuilder) {
    this.jda = jda;
    this.jdaBuilder = jdaBuilder;
  }

  public void setStatus(String status) {
    jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(status));
  }

  public void setGame(String game) {
    jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(game));
  }

  public void setPlaying(String game) {
    setGame(game);
  }

  public void setWatching(String game) {
    setStatus(game);
  }

  public void setListening(String music) {
    jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.listening(music));
  }

  public void changePresenceText(String text) {
    Presence presence = jda.getPresence();
    Activity activity = presence.getActivity();

    Activity.ActivityType type = getActivityType();
    jda.getPresence().setPresence(presence.getStatus(), Activity.of(type, text));
  }

  public void changePresenceStatus(OnlineStatus status) {
    jda.getPresence().setPresence(status, Activity.of(getActivityType(), getPresentText()));
  }

  public Activity.ActivityType getActivityType() {
    Activity activity = jda.getPresence().getActivity();
    return activity == null ? Activity.ActivityType.LISTENING : activity.getType();
  }

  public String getPresentText() {
    Activity activity = jda.getPresence().getActivity();

    if (activity == null) {
      return "unknown";
    } else {
      String name = activity.getName();
      return Strings.isNullOrEmpty(name) ? "unknown" : name;
    }
  }

  public void registerListener(Object eventListener) {
    jda.addEventListener(eventListener);
  }

  public void registerListeners(Object... eventListeners) {
    for (Object eventListener : eventListeners) {
      registerListener(eventListener);
    }
  }

  public void shutdown() {
    jda.shutdown();
  }

  public void log(String message) {}
}
