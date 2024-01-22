package me.syncwrld.booter;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

public class CommonPurposeClass {

  public InputStream getResource(String resource, Class<?> clazz) {
    Preconditions.checkNotNull(resource, "Resource cannot be null");
    Preconditions.checkNotNull(clazz, "Resource Provider Class cannot be null");

    URL resURL = clazz.getResource(resource);

    if (resURL == null) {
      return null;
    }
    URLConnection urlConnection = null;
    try {
      urlConnection = resURL.openConnection();
      urlConnection.setUseCaches(false);
      return urlConnection.getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void closeQuietly(InputStream inputStream) {
    Preconditions.checkNotNull(inputStream, "Input Stream cannot be null");
    try {
      inputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public OutputStream getOutputStream(String resource, Class<?> clazz) {
    Preconditions.checkNotNull(resource, "Resource cannot be null");
    Preconditions.checkNotNull(clazz, "Resource Provider Class cannot be null");

    URL resURL = clazz.getResource(resource);

    if (resURL == null) {
      return null;
    }

    URLConnection urlConnection = null;
    try {
      urlConnection = resURL.openConnection();
      urlConnection.setUseCaches(false);
      return urlConnection.getOutputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean currentOsEquals(OperatingSystem operatingSystem) {
    Preconditions.checkNotNull(operatingSystem, "Compared operating system cannot be null");
    return getOs().equalsIgnoreCase(operatingSystem.getName());
  }

  public boolean currentOsVersionEquals(String version) {
    Preconditions.checkNotNull(version, "Compared version cannot be null");
    return getOsVersion().equalsIgnoreCase(version);
  }

  public String getOs() {
    return System.getProperty("os.name");
  }

  public String getOsVersion() {
    return System.getProperty("os.version");
  }

  public String getOsArch() {
    return System.getProperty("os.arch");
  }

  public String getJavaVersion() {
    return System.getProperty("java.version");
  }

  public String getJavaVendor() {
    return System.getProperty("java.vendor");
  }

  public String getJavaHome() {
    return System.getProperty("java.home");
  }

  public boolean createDir(String path) {
    Preconditions.checkNotNull(path, "Path cannot be null");
    return new java.io.File(formatPath(path)).mkdirs();
  }

  public boolean deleteDir(String path) {
    Preconditions.checkNotNull(path, "Path cannot be null");
    return new java.io.File(formatPath(path)).delete();
  }

  public boolean deleteFile(String path) {
    Preconditions.checkNotNull(path, "Path cannot be null");
    return new java.io.File(formatPath(path)).delete();
  }

  public boolean createFile(String path) {
    Preconditions.checkNotNull(path, "Path cannot be null");
    try {
      return new java.io.File(formatPath(path)).createNewFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String formatPath(String path) {
    Preconditions.checkNotNull(path, "Path cannot be null");
    return path.replace("/", File.separator)
        .replace("\\", File.separator)
        .replace("@dir", System.getProperty("user.dir"));
  }

  public boolean saveFile(String path, String file) {
    Preconditions.checkNotNull(path, "Path cannot be null");
    Preconditions.checkNotNull(file, "File cannot be null");

    InputStream resource = getResource(file, CommonPurposeClass.class);
    if (resource == null) {
      return false;
    }

    File pathFile = new File(formatPath(path));
    if (!pathFile.exists()) {
      if (!pathFile.mkdirs()) {
        return false;
      }
    }

    try {
      Files.copy(resource, pathFile.toPath());
      return true;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
