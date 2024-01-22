package me.syncwrld.booter.minecraft.loader;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import lombok.experimental.Tolerate;
import me.syncwrld.booter.minecraft.ConstantData;
import me.syncwrld.booter.minecraft.tool.etc.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BukkitPlugin extends JavaPlugin {

  protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  protected final Collection<ConstantData> dataCollection = new ArrayList<>();
  protected String[] asciiArt = {};
  protected FileConfiguration configuration = this.getConfig();
  private String prefix = "";
  private boolean autoDataRegistry = true;

  public static void callEvent(Event event) {
    Bukkit.getServer().getPluginManager().callEvent(event);
  }

  public static void callEvents(Event... events) {
    for (Event event : events) {
      callEvent(event);
    }
  }

  protected abstract void whenLoad();

  protected abstract void whenEnable();

  protected abstract void whenDisable();

  public File getConfigFile(String name) {
    name = name.endsWith(".yml") ? name : name + ".yml";
    return new File(this.getDataFolder(), name);
  }

  @Override
  public void onLoad() {
    this.whenLoad();
  }

  @Override
  public void onEnable() {
    this.whenEnable();

    if (autoDataRegistry) this.dataCollection.forEach(ConstantData::load);
  }

  @Override
  public void onDisable() {
    this.whenDisable();
  }

  public void log(String message) {
    message = message.replace("&", "§");
    message = !(Strings.isNullOrEmpty(prefix)) ? String.format("%s %s", prefix, message) : message;
    this.server().getConsoleSender().sendMessage(message);
  }

  public void log(String... messages) {
    for (String message : messages) {
      this.log(message);
    }
  }

  public void log(int num, String message) {
    for (int i = 0; i < num; i++) {
      this.log(message);
    }
  }

  public void log(String colorCode, String message) {
    this.log(String.format("§%s%s", colorCode, message).replace("&", "§"));
  }

  public void log(String colorCode, String[] messages) {
    for (String message : messages) {
      this.log(colorCode, message);
    }
  }

  public void registerListener(Listener listener) {
    this.pluginManager().registerEvents(listener, this);
  }

  public void registerListeners(Listener... listeners) {
    for (Listener listener : listeners) {
      this.registerListener(listener);
    }
  }

  @Deprecated
  public FileConfiguration getConfig() {
    return super.getConfig();
  }

  public FileConfiguration getConfigOf(String config) {
    config = config.endsWith(".yml") ? config : config + ".yml";
    return YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), config));
  }

  public Server server() {
    return this.getServer();
  }

  public PluginManager pluginManager() {
    return this.server().getPluginManager();
  }

  public void saveFile(String file) {
    if (this.getResource(file) != null) {
      this.saveResource(file, false);
    }
  }

  public void startRepeatingRunnable(Runnable runnable, long delay) {
    this.server().getScheduler().runTaskTimer(this, runnable, 0, delay);
  }

  public void runLater(Runnable runnable, long seconds) {
    this.server().getScheduler().runTaskLater(this, runnable, seconds * 20);
  }

  public void saveConfig(String config) {
    config = config.endsWith(".yml") ? config : config + ".yml";
    this.saveFile(config);
  }

  public void saveFile(String file, String path) {
    if (this.getResource(file) != null) {
      File completePath = new File(path);
      if (!completePath.exists()) if (!completePath.mkdirs()) return;

      File completeFile = new File(completePath, file);
      if (!completeFile.exists()) this.saveFile(file);
    }
  }

  @Override
  public void saveResource(String resourcePath, boolean replace) {
    if (resourcePath == null || resourcePath.isEmpty()) {
      throw new IllegalArgumentException("ResourcePath cannot be null or empty");
    }

    resourcePath = resourcePath.replace('\\', '/');
    InputStream inputStream = this.getResource(resourcePath);

    if (inputStream == null) return;

    File outFile = new File(this.getDataFolder(), resourcePath);
    int lastIndex = resourcePath.lastIndexOf('/');
    File outDir = new File(this.getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

    if (!outDir.exists()) {
      outDir.mkdirs();
    }

    try {
      if (outFile.exists() && !replace) {
        return;
      }
      try (OutputStream out = Files.newOutputStream(outFile.toPath())) {
        byte[] buf = new byte[1024];
        int len;

        while ((len = inputStream.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public boolean hasSpigot() {
    try {
      Class.forName("org.spigotmc.spigot.Main");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  @Override
  public void saveDefaultConfig() {
    this.saveFile("configuration.yml");
  }

  @Override
  public void saveConfig() {
    this.saveFile("configuration.yml");
    this.configuration = this.getConfig();
  }

  public String serialize(Object object) {
    if (object instanceof Location) {
      return LocationSerializer.serialize((Location) object);
    }

    if (object instanceof ItemStack) {
      ItemStack itemStack = (ItemStack) object;
      TypeToken<ItemStack> typeToken = new TypeToken<ItemStack>() {};
      return gson.toJson(itemStack, typeToken.getType());
    }

    return null;
  }

  public Object deserialize(String string, String type) {
    if (string == null) {
      return null;
    }

    type = type.toLowerCase(Locale.ROOT);
    if ("location".equals(type)) {
      return LocationSerializer.deserialize(string);
    }

    if ("itemstack".equals(type)) {
      TypeToken<ItemStack> typeToken = new TypeToken<ItemStack>() {};
      return gson.fromJson(string, typeToken.getType());
    }

    return null;
  }

  public Location deserializeLocation(String string) {
    return (Location) this.deserialize(string, "location");
  }

  public ItemStack deserializeItemStack(String string) {
    return (ItemStack) this.deserialize(string, "itemstack");
  }

  public void setAsciiArt(String... art) {
    this.asciiArt = art;
  }

  public void sendAsciiArt() {
    if (this.asciiArt == null) {
      return;
    }

    this.sendAsciiArt("f");
  }

  public void sendAsciiArt(String colorCode) {
    if (this.asciiArt == null) {
      return;
    }

    for (String line : this.asciiArt) {
      this.log(colorCode, line);
    }
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix.replace("&", "§");
  }

  public void registerData(ConstantData constantData) {
    this.dataCollection.add(constantData);
  }

  public void registerDatas(ConstantData... constantDatas) {
    for (ConstantData constantData : constantDatas) {
      registerData(constantData);
    }
  }

  @Tolerate
  public void setAutoDataRegistry(boolean b) {
    this.autoDataRegistry = b;
  }
}
