package me.syncwrld.booter.minecraft.dependency;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.syncwrld.booter.minecraft.BooterConstants;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
@Getter
public class Dependencies {

  private Queue<CompletableFuture<Void>> downloadQueue;
  private Plugin plugin;
  private ScheduledExecutorService executor;

  public static Dependencies of(Plugin plugin) {
    return new Dependencies(
        new ConcurrentLinkedQueue<>(), plugin, Executors.newSingleThreadScheduledExecutor());
  }

  public static Dependency[] of(File file) {
    try (Reader reader = new FileReader(file)) {
      JsonReader jsonReader = new JsonReader(reader);
      JsonObject jsonObject = BooterConstants.GSON.fromJson(jsonReader, JsonObject.class);
      List<Dependency> dependencies = new ArrayList<>();

      for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        String key = entry.getKey();
        JsonArray dependencyArray = entry.getValue().getAsJsonArray();

        if (dependencyArray.size() >= 3) {
          DependencyInfo dependencyInfo =
              new DependencyInfo(
                  dependencyArray.get(0).getAsString(),
                  dependencyArray.get(1).getAsString(),
                  dependencyArray.get(2).getAsString());
          dependencies.add(new Dependency(dependencyInfo));
        } else {
          System.err.println("Array de dependência incompleto para chave: " + key);
        }
      }

      return dependencies.toArray(new Dependency[0]);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return new Dependency[0];
  }

  public void queue(Dependency dependency) {
    try {
      this.downloadQueue.add(dependency.download());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void queue(Dependency... dependencies) {
    for (Dependency dependency : dependencies) {
      this.queue(dependency);
    }
  }

  public void remove(Dependency dependency) {
    this.downloadQueue.remove(dependency.download());
  }

  public void runQueue() {
    CompletableFuture<Void> voidQueue = CompletableFuture.completedFuture(null);
    while (!this.downloadQueue.isEmpty()) {
      CompletableFuture<Void> poll = downloadQueue.poll();
      voidQueue = voidQueue.thenCompose(__ -> poll);
    }
    voidQueue.join();
  }
}
