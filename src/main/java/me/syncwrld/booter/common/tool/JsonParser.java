package me.syncwrld.booter.common.tool;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.*;
import lombok.SneakyThrows;
import me.syncwrld.booter.Booter;
import me.syncwrld.booter.Constants;

public class JsonParser {

  @SneakyThrows
  public JsonObject getObject(File file) {
    if (!(file.exists())) {
      Booter.getLogger().info("File not found: " + file.getAbsolutePath());
    }
    try (Reader reader = new FileReader(file)) {
      JsonReader jsonReader = new JsonReader(reader);
      return Constants.GSON.fromJson(jsonReader, JsonObject.class);
    }
  }

  @SneakyThrows
  public JsonObject getObject(InputStream inputStream) {
    InputStreamReader reader = new InputStreamReader(inputStream);
    try (Reader readerObj = new BufferedReader(reader)) {
      JsonReader jsonReader = new JsonReader(readerObj);
      return Constants.GSON.fromJson(jsonReader, JsonObject.class);
    }
  }
}
