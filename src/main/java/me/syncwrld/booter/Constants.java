package me.syncwrld.booter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.logging.Logger;

public class Constants {
  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static String NAME = "syncBooter";
  public static Logger LOGGER = Booter.getLogger();
}
