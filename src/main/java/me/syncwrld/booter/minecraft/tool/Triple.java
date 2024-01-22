package me.syncwrld.booter.minecraft.tool;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Triple<K, V, V2> {

    private K key;
    private V value;
    private V2 value2;

    public Triple(K key, V value, V2 value2) {
        this.key = key;
        this.value = value;
        this.value2 = value2;
    }

}
