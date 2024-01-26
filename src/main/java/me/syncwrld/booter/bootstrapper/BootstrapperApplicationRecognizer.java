package me.syncwrld.booter.bootstrapper;

import lombok.Data;
import lombok.experimental.Accessors;
import me.syncwrld.booter.ApplicationType;
import me.syncwrld.booter.bootstrapper.strategies.BukkitLoaderStrategy;

@Data
@Accessors(chain = true, fluent = true)
public class BootstrapperApplicationRecognizer {

  private ApplicationType applicationType;

  private BootstrapperApplicationRecognizer() {
    applicationType =
        this.applicationType == null ? ApplicationType.GENERAL_JAVA : this.applicationType;
  }

  public void applyBootstrapper() {
    LoaderStrategy<?> loaderStrategy;
    if (applicationType == ApplicationType.BUKKIT_PLUGIN) {
      loaderStrategy = new BukkitLoaderStrategy();
      return;
    }

    if (applicationType == ApplicationType.VELOCITY_PLUGIN) {
      loaderStrategy = new BukkitLoaderStrategy();
      return;
    }
  }
}
