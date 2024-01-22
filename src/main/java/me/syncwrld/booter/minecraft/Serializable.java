package me.syncwrld.booter.minecraft;

public interface Serializable {
  default String getAsJson() {
    return BooterConstants.GSON.toJson(this, this.getClass());
  }
}
