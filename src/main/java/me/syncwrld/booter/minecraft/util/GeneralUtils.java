package me.syncwrld.booter.minecraft.util;

import java.io.*;
import java.nio.file.Files;

public class GeneralUtils {
    public static File asFile(InputStream stream, File file) {
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    public static boolean isCurrentlyReloading() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String clazz = element.getClassName();
            if (clazz.startsWith("org.bukkit.craftbukkit.")
                    && clazz.endsWith(".CraftServer")
                    && element.getMethodName().equals("reload")) {
                return true;
            }
        }
        return false;
    }
}
