package me.syncwrld.booter.minecraft.tool.global;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.syncwrld.booter.minecraft.Serializable;

@Getter
public class FancyTime implements Serializable {
  private final int days;
  private final int hours;
  private final int minutes;
  private final int seconds;

  public FancyTime(int days, int hours, int minutes, int seconds) {
    Preconditions.checkArgument(seconds > 60 || seconds < 0, "seconds must be between 0 and 60");
    Preconditions.checkArgument(minutes > 60 || minutes < 0, "minutes must be between 0 and 60");
    Preconditions.checkArgument(hours < 0, "hours must be positive");
    Preconditions.checkArgument(days < 0, "days must be positive");

    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
  }

  public FancyTime(String string) {
    this.days = fromString(string).getDays();
    this.hours = fromString(string).getHours();
    this.minutes = fromString(string).getMinutes();
    this.seconds = fromString(string).getSeconds();
  }

  private FancyTime fromString(String string) {
    Preconditions.checkNotNull(string, "string cannot be null");

    String[] inputArgs = string.split(" ");

    int days = 0, hours = 0, minutes = 0, seconds = 0;

    for (String current : inputArgs) {
      String lowerCaseCurrent = current.toLowerCase();

      int parsed = Integer.parseInt(lowerCaseCurrent.substring(0, lowerCaseCurrent.length() - 1));
      switch (lowerCaseCurrent.charAt(lowerCaseCurrent.length() - 1)) {
        case 's':
          seconds += parsed;
          break;
        case 'm':
          minutes += parsed;
          break;
        case 'h':
          hours += parsed;
          break;
        case 'd':
          days += parsed;
      }
    }

    return new FancyTime(days, hours, minutes, seconds);
  }

  public long toTicks() {
    int dayTicks = days * 60 * 60 * 24 * 20;
    int hourTicks = hours * 60 * 60 * 20;
    int minuteTicks = minutes * 60 * 20;
    int secondsTicks = seconds * 20;

    return (long) dayTicks + hourTicks + minuteTicks + secondsTicks;
  }
}
