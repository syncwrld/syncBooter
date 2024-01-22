package me.syncwrld.booter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface ApplicationBootstrapper {
  void enable();

  void disable();

  default void start(ApplicationLoader appLoader) {
    Method method = null;
    try {
      method = appLoader.getClass().getMethod("setBootstrapper", ApplicationBootstrapper.class);
      method.setAccessible(true);

      method.invoke(appLoader, this);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
