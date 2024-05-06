package me.syncwrld.booter.minecraft.registry;

import static org.reflections.scanners.Scanners.SubTypes;

import java.io.InvalidClassException;
import java.util.Set;
import me.syncwrld.booter.minecraft.loader.BukkitPlugin;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

public class MinecraftRegistry {

  private final Class<? extends BukkitPlugin> pluginClass;

  public MinecraftRegistry(Class<? extends BukkitPlugin> pluginClass) {
    this.pluginClass = pluginClass;
  }

  public void queueAutoRegistry(SupportedType supportedType) throws InvalidClassException {
    String packageName = pluginClass.getPackage().getName().trim().replace("/", ".");
    Reflections reflections = new Reflections(packageName);

    Set<Class<?>> classes = reflections.get(SubTypes.of(Listener.class).asClass());

    for (Class<?> clazz : classes) {
      if (clazz.getAnnotation(AutoRegistry.class) == null) {
        throw new InvalidClassException("Class " + clazz.getName() + " is not annotated with @AutoRegistry");
      }
    }
  }
}
