package me.syncwrld.booter.minecraft.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class MinecraftView {
  private final Inventory inventory;
  private final String name;
  private final int rows;
  private final boolean closeable;

  private MinecraftView(Inventory inventory, String name, int rows, boolean closeable) {
    this.inventory = inventory;
    this.name = name;
    this.rows = rows;
    this.closeable = closeable;
  }

  public static MinecraftView makeView(String name, int rows, boolean closeable) {
    Inventory inventory = Bukkit.createInventory(null, rows * 9, name);
    return null;
  }

  abstract void whenRender(Player player);

  abstract void whenClose(Player player);

  abstract void configureInventory(ViewConfiguration viewConfiguration);
}
