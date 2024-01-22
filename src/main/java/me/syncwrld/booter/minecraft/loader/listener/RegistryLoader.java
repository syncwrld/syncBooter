package me.syncwrld.booter.minecraft.loader.listener;

import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import me.syncwrld.booter.minecraft.loader.BukkitPlugin;
import me.syncwrld.booter.minecraft.sample.RegistryLoaderType;
import org.bukkit.event.Listener;

public class RegistryLoader {

  private final Collection<RegistryLoaderType> registeredTypes = new ArrayList<>();

  private final BukkitPlugin plugin;
  private final String _package;
  private final Class<?> _initializer;
  private int registeredListeners = 0;

  private RegistryLoader(BukkitPlugin plugin, String _package, Class<?> _initializer) {
    this.plugin = plugin;
    this._package = _package;
    this._initializer = _initializer;
  }

  public static RegistryLoader of(BukkitPlugin plugin, String _package, Class<?> _initializer) {
    return new RegistryLoader(plugin, _package, _initializer);
  }

  @SuppressWarnings("UnstableApiUsage")
  private int registerEvents(BukkitPlugin plugin, String _package, Class<?> _initializer) {
    AtomicInteger registered = new AtomicInteger();
    try {
      ClassPath classPath = ClassPath.from(_initializer.getClassLoader());
      Set<ClassPath.ClassInfo> listenersClasses =
          classPath.getAllClasses().parallelStream()
              .filter(
                  clazz ->
                      clazz.getPackageName().replace("/", ".").startsWith(_package)
                          && (clazz.getPackageName().contains("listener")
                              || clazz.getPackageName().contains("event")))
              .collect(Collectors.toSet());

      if (listenersClasses == null || listenersClasses.isEmpty())
        return 0;

      listenersClasses.forEach(
          classInfo -> {
            Class<?> loadedClass = classInfo.load();
            if (!isListenerClass(loadedClass)) return;

            Listener listener;

            try {
              Constructor<?> initializerConstructor = loadedClass.getDeclaredConstructor(_initializer);
              initializerConstructor.setAccessible(true);
              listener = (Listener) initializerConstructor.newInstance(_initializer);
              plugin.registerListener(listener);
              registered.getAndIncrement();

              plugin.log("4", "[1 - LC] Registered listener: " + loadedClass.getName());
            } catch (Exception e) {
              Object rawListener = null;
              try {
                rawListener = loadedClass.newInstance();
                if (rawListener instanceof Listener) {
                  listener = (Listener) rawListener;
                  plugin.registerListener(listener);

                  registered.getAndIncrement();
                  plugin.log("4", "[2 - LC] Registered listener: " + loadedClass.getName());
                }
              } catch (Exception ex) {
                throw new RuntimeException(ex);
              }
            }
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    this.registeredListeners = registered.get();
    return registered.get();
  }

  boolean isListenerClass(Class<?> clazz) {
    return Arrays.asList(clazz.getInterfaces()).contains(Listener.class);
  }

  public void add(RegistryLoaderType type) {
    this.registeredTypes.add(type);
  }

  public void load() {
    this.registeredTypes.forEach(
        type -> {
          switch (type) {
            case LISTENER:
              registerEvents(this.plugin, this._package, this._initializer);
              break;
            case COMMAND:
              break;
            case ACF_COMMAND:
              break;
            case DEPENDENCY:
              break;
          }
        });
  }

  public CompletableFuture<Integer> getRegisteredListeners() {
    return CompletableFuture.completedFuture(this.registeredListeners);
  }
}
