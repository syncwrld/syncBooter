package me.syncwrld.booter.minecraft.dependency;

import com.google.common.base.Stopwatch;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@Getter
public class Dependency {

  private final DependencyInfo dependencyInfo;
  private final String name;
  private final String version;
  private final String downloadURL;
  private long bytesSize = 0L;

  public Dependency(DependencyInfo dependencyInfo) {
    this.name = dependencyInfo.getName();
    this.version = dependencyInfo.getVersion();
    this.downloadURL = dependencyInfo.getDownloadURL();
    this.dependencyInfo = dependencyInfo;
  }

  private static String asReadableSize(long byteSize) {
    if (byteSize < 1024) {
      return byteSize + " B";
    } else if (byteSize < 1024 * 1024) {
      return (byteSize / 1024) + " KB";
    } else if (byteSize < 1024 * 1024 * 1024) {
      return (byteSize / (1024 * 1024)) + " MB";
    } else {
      return (byteSize / (1024 * 1024 * 1024)) + " GB";
    }
  }

  public CompletableFuture<Void> download() {
    return CompletableFuture.runAsync(
        () -> {
          if (Bukkit.getPluginManager().getPlugin(name) != null) return;

          Stopwatch stopwatch = Stopwatch.createStarted();
          String $s = System.getProperty("file.separator");
          String pathName = System.getProperty("user.dir") + $s + "plugins";
          URL url = null;

          try {
            url = new URL(downloadURL);
          } catch (MalformedURLException e) {
            Bukkit.getConsoleSender()
                .sendMessage(
                    "§c[syncBooter] Failed to download dependency: "
                        + name
                        + " - Cause: Invalid Download URL");
            return;
          }

          try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            try {
              Files.copy(
                  inputStream,
                  new File(pathName + $s + name.toLowerCase() + "-" + version + ".jar").toPath());
            } catch (IOException e) {
              Bukkit.getConsoleSender()
                  .sendMessage(
                      "§f[syncBooter] Unable to download dependency: "
                          + name
                          + " because it's already downloaded.");
              return;
            }

            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

            executorService.schedule(
                () -> {
                  File pluginFile =
                      new File(pathName + $s + name.toLowerCase() + "-" + version + ".jar");
                  this.bytesSize = pluginFile.length();

                  try {
                    Plugin loadedPlugin = Bukkit.getPluginManager().loadPlugin(pluginFile);
                    Bukkit.getPluginManager().enablePlugin(loadedPlugin);

                    if (name.equalsIgnoreCase("PlaceholderAPI")) {
                      Bukkit.getConsoleSender()
                          .sendMessage(
                              "§c[syncBooter] PlaceholderAPI has been successfully installed, but some errors will be shown on console, to solve it restart the server!");
                    }
                  } catch (Exception e) {
                    Bukkit.getConsoleSender()
                        .sendMessage(
                            "§c[syncBooter] Failed to start dependency: "
                                + name
                                + " - Cause: "
                                + e.getMessage()
                                + " | The server will restart soon!");
                  }
                },
                1,
                TimeUnit.MILLISECONDS);

            Bukkit.getConsoleSender()
                .sendMessage(
                    "§a[syncBooter] Downloaded dependency: "
                        + name
                        + " - Version: "
                        + version
                        + " - Download time was: "
                        + stopwatch.toString()
                        + " ⚡. + ("
                        + asReadableSize(bytesSize)
                        + ")");
          } catch (IOException e) {
            Bukkit.getConsoleSender()
                .sendMessage(
                    "§a[syncBooter] §cWas not able to download dependency: "
                        + name
                        + " v"
                        + version
                        + " - Cause: "
                        + e.getMessage());
          }
        });
  }

  @Override
  public String toString() {
    return "Dependency{"
        + "name='"
        + name
        + '\''
        + ", version='"
        + version
        + '\''
        + ", downloadURL='"
        + downloadURL
        + '\''
        + '}';
  }
}
