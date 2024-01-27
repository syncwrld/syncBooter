package me.syncwrld.booter.minecraft.app;

import me.syncwrld.booter.ApplicationType;
import me.syncwrld.booter.application.AppConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MinecraftAppDefaultConfigurer {

  public AppConfiguration autoConfigure(Object object) {
    if (object instanceof JavaPlugin) {
      JavaPlugin plugin = (JavaPlugin) object;
      return new AppConfiguration() {
        @Override
        public ApplicationType appType() {
          return ApplicationType.BUKKIT_PLUGIN;
        }

        @Override
        public String appName() {
          return plugin.getDescription().getName();
        }

        @Override
        public String appDescription() {
          return plugin.getDescription().getDescription();
        }

        @Override
        public String appVersion() {
          return plugin.getDescription().getVersion();
        }
      };
    }
  }
}
