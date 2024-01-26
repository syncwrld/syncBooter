package me.syncwrld.booter.bootstrapper.data;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.Data;
import me.syncwrld.booter.ApplicationType;

@Data
public class ApplicationInformation {
  private final ApplicationType appType;
  private final String name;
  private final String version;
  private final String description;
  private final String author;
  private final String libsPath;

  private ApplicationInformation(
      ApplicationType appType,
      String name,
      String version,
      String description,
      String author,
      String libsPath) {
    this.appType = appType;
    this.name = name;
    this.version = version;
    this.description = description;
    this.author = author;
    this.libsPath = libsPath;
  }

  public static ApplicationInformation catchFrom(ApplicationType type, JsonObject jsonObject) {
    return new ApplicationInformation(
        type,
        jsonObject.get("name").getAsString(),
        jsonObject.get("version").getAsString(),
        jsonObject.get("description").getAsString(),
        jsonObject.get("author").getAsString(),
        jsonObject.get("libsPath").getAsString());
  }

  public static ApplicationInformation catchFrom(ApplicationType type, InputStream yamlConfig) {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(yamlConfig));
    YamlReader yamlReader = new YamlReader(bufferedReader);

    return new ApplicationInformation(
        type,
        (String) yamlReader.get("name"),
        (String) yamlReader.get("version"),
        (String) yamlReader.get("description"),
        (String) yamlReader.get("author"),
        (String) yamlReader.get("libsPath"));
  }
}
