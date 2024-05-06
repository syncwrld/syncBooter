package me.syncwrld.booter.minecraft.registry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.minecraft.loader.BukkitPlugin;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRegistry {
    Class<? extends BukkitPlugin> pluginClass();
}
