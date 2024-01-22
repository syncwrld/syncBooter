package me.syncwrld.booter.minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BooterConstants {
  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static String BASE_PACKAGE = "me.syncwrld";

  public static void setBasePackage(String newPackage) {
    BooterConstants.BASE_PACKAGE = newPackage;
  }
}
