package me.syncwrld.booter.minecraft.tool.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
    private final ItemStack is;

    public ItemBuilder(final Material m) {
        this(m, 1);
    }

    public ItemBuilder(final ItemStack is) {
        this.is = is;
    }

    public ItemBuilder(final Material m, final int quantia) {
        this.is = new ItemStack(m, quantia);
    }

    public ItemBuilder(final Material m, final int quantia, final byte durabilidade) {
        this.is = new ItemStack(m, quantia, durabilidade);
    }

    public ItemBuilder(final Material m, final int quantia, final int durabilidade) {
        this.is = new ItemStack(m, quantia, (short) durabilidade);
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this.is);
    }

    public ItemBuilder setDurability(final short durabilidade) {
        this.is.setDurability(durabilidade);
        return this;
    }

    public ItemBuilder setAmount(final int amount) {
        this.is.setAmount(amount);
        final ItemMeta im = this.is.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setDurability(final int durabilidade) {
        this.is.setDurability(Short.valueOf(String.valueOf(durabilidade)));
        return this;
    }

    public ItemBuilder setName(final String nome) {
        final ItemMeta im = this.is.getItemMeta();
        im.setDisplayName(nome.replace("&", "§"));
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(final Enchantment ench, final int level) {
        this.is.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder removeEnchantment(final Enchantment ench) {
        this.is.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder setSkullOwner(final String dono) {
        try {
            final SkullMeta im = (SkullMeta) this.is.getItemMeta();
            im.setOwner(dono);
            this.is.setItemMeta(im);
        } catch (ClassCastException ex) {
        }
        return this;
    }

    public ItemBuilder addEnchant(final Enchantment ench, final int level) {
        final ItemMeta im = this.is.getItemMeta();
        im.addEnchant(ench, level, true);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantments(final Map<Enchantment, Integer> enchantments) {
        this.is.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder setInfinityDurability() {
        this.is.setDurability((short) 32767);
        return this;
    }

    public ItemBuilder addItemFlag(final ItemFlag flag) {
        final ItemMeta im = this.is.getItemMeta();
        im.addItemFlags(flag);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(final String... lore) {
        final ItemMeta im = this.is.getItemMeta();
        im.setLore(Arrays.stream(lore).map(s -> s.replace("&", "§")).collect(Collectors.toList()));
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(final List<String> lore) {
        final ItemMeta im = this.is.getItemMeta();
        im.setLore(lore.stream().map(s -> s.replace("&", "§")).collect(Collectors.toList()));
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(final String linha) {
        final ItemMeta im = this.is.getItemMeta();
        final List<String> lore = new ArrayList<String>(im.getLore());
        if (!lore.contains(linha)) {
            return this;
        }
        lore.remove(linha);
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(final int index) {
        final ItemMeta im = this.is.getItemMeta();
        final List<String> lore = new ArrayList<String>(im.getLore());
        if (index < 0 || index > lore.size()) {
            return this;
        }
        lore.remove(index);
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(final String linha) {
        final ItemMeta im = this.is.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (im.hasLore()) {
            lore = new ArrayList<String>(im.getLore());
        }
        lore.add(linha.replace("&", "§"));
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLores(final List<String> linha) {
        final ItemMeta im = this.is.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (im.hasLore()) {
            lore = new ArrayList<String>(im.getLore());
        }
        for (final String s : linha) {
            lore.add(s.replace("&", "§"));
        }
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(final String linha, final int pos) {
        final ItemMeta im = this.is.getItemMeta();
        final List<String> lore = new ArrayList<String>(im.getLore());
        lore.set(pos, linha.replace("&", "§"));
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setDyeColor(final DyeColor cor) {
        this.is.setDurability(cor.getData());
        return this;
    }

    @Deprecated
    public ItemBuilder setWoolColor(final DyeColor cor) {
        if (!this.is.getType().equals(Material.WOOL)) {
            return this;
        }
        this.is.setDurability(cor.getData());
        return this;
    }

    public ItemBuilder setLeatherArmorColor(final Color cor) {
        try {
            final LeatherArmorMeta im = (LeatherArmorMeta) this.is.getItemMeta();
            im.setColor(cor);
            this.is.setItemMeta(im);
        } catch (ClassCastException ex) {
        }
        return this;
    }

    public ItemStack create() {
        return this.is;
    }
}
