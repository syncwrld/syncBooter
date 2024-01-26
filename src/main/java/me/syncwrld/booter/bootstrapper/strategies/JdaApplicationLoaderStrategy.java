package me.syncwrld.booter.bootstrapper.strategies;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import me.syncwrld.booter.ApplicationType;
import me.syncwrld.booter.CommonPurposeClass;
import me.syncwrld.booter.bootstrapper.LoaderStrategy;
import me.syncwrld.booter.bootstrapper.data.ApplicationInformation;
import me.syncwrld.booter.common.tool.JsonParser;
import me.syncwrld.booter.discord.JdaApplication;
import me.syncwrld.booter.minecraft.tool.Pair;

public class JdaApplicationLoaderStrategy extends CommonPurposeClass
    implements LoaderStrategy<JdaApplication> {
  private final Set<Pair<File, String>> invalids;

  public JdaApplicationLoaderStrategy() {
    this.invalids = new HashSet<>();
  }

  @Override
  public Set<JdaApplication> getValid() {
    return null;
  }

  @Override
  public boolean isValid(Object o) {
    if (!(o instanceof File)) return false;
    File file = (File) o;
    String fileName = file.getName();

    if (!fileName.endsWith(".jar")) {
      invalids.add(new Pair<>(file, "Not a .jar file"));
      return false;
    }

    JarFile jarFile;

    try {
      jarFile = new JarFile(file);

      ZipEntry entry = jarFile.getEntry(formatPath("/booter/app.json"));

      if (entry == null) {
        invalids.add(new Pair<>(file, "Missing app.json"));
        return false;
      }

      InputStream inputStream = jarFile.getInputStream(entry);

      if (inputStream == null) {
        invalids.add(new Pair<>(file, "Invalid app.json"));
        return false;
      }

      JsonObject config = new JsonParser().getObject(inputStream);
      ApplicationInformation applicationInformation =
          ApplicationInformation.catchFrom(ApplicationType.JDA_APP, config);

      this.sendToNettyServer(applicationInformation);
    } catch (Exception e) {
      invalids.add(new Pair<>(file, e.getMessage()));
      return false;
    }

    return false;
  }

  @Override
  public JdaApplication recognize(Object object) {
    return null;
  }

  @Override
  public Set<Object> getPossible(File path) {
    return null;
  }

  @Override
  public Set<Pair<File, String>> getInvalidsWithReason() {
    return null;
  }

  @Override
  public boolean load(JdaApplication jdaApplication) {
    return false;
  }

  @Override
  public boolean loadAllValid() {
    return false;
  }

  @Override
  public File getLoadingFolder() {
    return new File(formatPath("@dir/booter-bots"));
  }

  @Override
  public String getApplicationIdentifier() {
    return "JDA (syncBooter) Bots";
  }

  private void openStream32() {}

  private void recognizeAndRunFile(File file, boolean packed) {}

  private JdaApplication createJdaApplication(Object o) {
    return null;
  }

  private void sendToNettyServer(ApplicationInformation appInfo) {}
}
