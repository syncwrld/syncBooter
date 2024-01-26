package me.syncwrld.booter.bootstrapper.strategies;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.base.Strings;
import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import me.syncwrld.booter.CommonPurposeClass;
import me.syncwrld.booter.bootstrapper.LoaderStrategy;
import me.syncwrld.booter.minecraft.tool.Pair;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitLoaderStrategy extends CommonPurposeClass implements LoaderStrategy<JavaPlugin> {
  private final Set<Pair<File, String>> invalids;

  public BukkitLoaderStrategy() {
    this.invalids = new HashSet<>();
  }

  @Override
  public Set<JavaPlugin> getValid() {
    final Set<JavaPlugin> recognizedPlugins = new HashSet<>();
    final Set<Object> possible = getPossible(new File(formatPath("@dir/booter-plugins")));

    for (Object o : possible) {
      if (!(o instanceof File)) continue;

      File file = (File) o;
      JarFile jarFile = null;

      try {
        jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          String entryName = entry.getName();

          if (Strings.isNullOrEmpty(entryName)) continue;

          if (entryName.equals("plugin.yml")) {
            InputStream inputStream = jarFile.getInputStream(entry);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            YamlReader yamlReader = new YamlReader(new BufferedReader(inputStreamReader));

            String mainClass = (String) yamlReader.get("main");

            if (Strings.isNullOrEmpty(mainClass)) {
              invalids.add(new Pair<>(file, "No main class found"));
            } else {
              try {
                Class<?> wrappedClass = Class.forName(mainClass);
                recognizedPlugins.add(JavaPlugin.getProvidingPlugin(wrappedClass));
              } catch (ClassNotFoundException e) {
                invalids.add(new Pair<>(file, e.getMessage()));
              }
            }

            break;
          }
        }
      } catch (IOException e) {
        invalids.add(new Pair<>(file, e.getMessage()));
      }
    }

    return recognizedPlugins;
  }

  @Override
  public boolean isValid(Object o) {
    if (!(o instanceof File)) return false;
    File file = (File) o;
    boolean jar = file.getName().endsWith(".jar");

    if (!jar) {
      invalids.add(new Pair<>(file, "Not a .jar file"));
    }

    return jar && recognize(file) != null;
  }

  @Override
  public JavaPlugin recognize(Object o) {
    if (!(o instanceof File)) return null;
    File file = (File) o;
    return getValid().stream()
        .filter(p -> file.getName().equals(p.getName()))
        .findFirst()
        .orElse(null);
  }

  @Override
  public Set<Object> getPossible(File path) {
    File[] files = path.listFiles();
    Set<File> plugins = new HashSet<>();

    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) continue;

        if (!(isValid(file))) continue;
        plugins.add(file);
      }
    }

    return Collections.singleton(plugins);
  }

  @Override
  public Set<Pair<File, String>> getInvalidsWithReason() {
    return invalids;
  }

  @Override
  public boolean load(JavaPlugin javaPlugin) {
    if (javaPlugin != null && !(javaPlugin.isEnabled())) {
      PluginManager pluginManager = Bukkit.getPluginManager();
      pluginManager.enablePlugin(javaPlugin);
      return true;
    }
    return false;
  }

  @Override
  public boolean loadAllValid() {
    boolean correctlyLoaded = true;

    for (JavaPlugin javaPlugin : getValid()) {
      if (!load(javaPlugin)) {
        correctlyLoaded = false;
      }
    }
    return correctlyLoaded;
  }

  @Override
  public File getLoadingFolder() {
    return new File(formatPath("@dir/booter-plugins"));
  }

  @Override
  public String getApplicationIdentifier() {
    return "Bukkit (syncBooter) Plugins";
  }
}
