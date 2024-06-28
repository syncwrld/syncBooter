package me.syncwrld.booter.minecraft;

import me.syncwrld.booter.minecraft.loader.BukkitPlugin;

public class SpigotLoader extends BukkitPlugin {
  @Override
  protected void whenLoad() {
    this.setPrefix("&b[syncBooter] ");
  }

  @Override
  protected void whenEnable() {
    this.log(
        "&eThis plugin will not execute any action on your server, he is only a development library.");
  }

  @Override
  protected void whenDisable() {}
}
