package me.syncwrld.booter.minecraft.dependency;

import lombok.Data;

@Data
public class DependencyInfo {
    private final String name;
    private final String version;
    private final String downloadURL;
}
