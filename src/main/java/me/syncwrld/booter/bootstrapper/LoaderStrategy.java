package me.syncwrld.booter.bootstrapper;

import java.io.File;
import java.util.Set;
import me.syncwrld.booter.minecraft.tool.Pair;

public interface LoaderStrategy<T> {
  Set<T> getValid();

  boolean isValid(Object o);

  T recognize(Object o);

  Set<Object> getPossible(File path);

  Set<Pair<File, String>> getInvalidsWithReason();

  boolean load(T t);

  boolean loadAllValid();

  File getLoadingFolder();

  String getApplicationIdentifier();
}
