package me.syncwrld.booter.minecraft.serialization;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import lombok.val;
import me.syncwrld.booter.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class Serializer {

  private final Gson gson = Constants.GSON;

  public String serializeInventory(InventoryType inventoryType, Inventory inventory) {
    ItemStack[] content = inventory.getContents();
    Map<Object, Object> serializationMap = new HashMap<>();
    Map<Integer, String> items = new HashMap<>();

    int index = 0;
    for (ItemStack stack : content) {
      if (stack == null || stack.getType() == Material.AIR) continue;

      Map<String, Object> itemData = new HashMap<>();

      // Item properties
      val type = stack.getType();
      val enchantments = stack.getEnchantments();
      int amount = stack.getAmount();
      val itemMeta = stack.getItemMeta();
      short durability = stack.getDurability();
      val data = stack.getData();

      itemData.put("type", gson.toJson(type, Material.class));
      if ((enchantments != null) && !enchantments.isEmpty())
        itemData.put("enchantments", gson.toJson(enchantments));
      if (data != null) itemData.put("data", gson.toJson(data, MaterialData.class));

      TypeToken<ItemMeta> typeToken = new TypeToken<ItemMeta>() {};
      if (stack.hasItemMeta()) itemData.put("meta", gson.toJson(itemMeta, typeToken.getType()));

      itemData.put("amount", gson.toJson(amount, Integer.class));
      itemData.put("durability", durability);

      items.put(index, gson.toJson(itemData));
      itemData.clear();

      index++;
    }

    serializationMap.put("type", gson.toJson(inventoryType));
    serializationMap.put("content", items);

    return gson.toJson(serializationMap);
  }

  public ItemStack[] deserializeInventoryContent(String serializedInventory) {
    Map<String, Object> deserializationMap = gson.fromJson(serializedInventory, HashMap.class);

    InventoryType inventoryType =
        gson.fromJson((String) deserializationMap.get("type"), InventoryType.class);
    Map<Object, String> itemsMap = gson.fromJson(gson.toJson(deserializationMap.get("content")), HashMap.class);

    ItemStack[] contents = new ItemStack[inventoryType.getDefaultSize()];

    for (Map.Entry<Object, String> entry : itemsMap.entrySet()) {
      int index = gson.fromJson(entry.getKey().toString(), Integer.class);
      String itemDataJson = entry.getValue();

      Map<String, Object> itemData = gson.fromJson(itemDataJson, HashMap.class);

      Material type = gson.fromJson((String) itemData.get("type"), Material.class);
      Map<Enchantment, Integer> enchantments =
          gson.fromJson((String) itemData.get("enchantments"), HashMap.class);
      int amount = gson.fromJson((String) itemData.get("amount"), Integer.class);
      ItemMeta itemMeta = gson.fromJson((String) itemData.get("meta"), ItemMeta.class);
      MaterialData data = gson.fromJson((String) itemData.get("data"), MaterialData.class);

      ItemStack itemStack = new ItemStack(type, amount);

      if (itemMeta != null) itemStack.setItemMeta(itemMeta);

      if (data != null) itemStack.setData(data);

      if (enchantments != null) itemStack.addUnsafeEnchantments(enchantments);

      contents[index] = itemStack;
    }

    return contents;
  }

  public Inventory deserializeInventory(String serializedInventory) {
    Map<String, Object> deserializationMap = gson.fromJson(serializedInventory, HashMap.class);

    InventoryType inventoryType =
        gson.fromJson((String) deserializationMap.get("type"), InventoryType.class);
    Map<Integer, String> itemsMap =
        gson.fromJson(gson.toJson(deserializationMap.get("content")), HashMap.class);

    Inventory inventory = Bukkit.createInventory(null, inventoryType);

    for (Map.Entry<Integer, String> entry : itemsMap.entrySet()) {
      int index = entry.getKey();
      String itemDataJson = entry.getValue();

      Map<String, Object> itemData = gson.fromJson(itemDataJson, HashMap.class);

      Map<Enchantment, Integer> enchantments =
          gson.fromJson((String) itemData.get("enchantments"), HashMap.class);
      int amount = gson.fromJson((String) itemData.get("amount"), Integer.class);
      ItemMeta itemMeta = gson.fromJson((String) itemData.get("meta"), ItemMeta.class);
      MaterialData data = gson.fromJson((String) itemData.get("data"), MaterialData.class);

      ItemStack itemStack = new ItemStack(data.getItemType(), amount);
      itemStack.setItemMeta(itemMeta);
      itemStack.setData(data);

      if (enchantments != null) itemStack.addUnsafeEnchantments(enchantments);

      inventory.setItem(index, itemStack);
    }

    return inventory;
  }

}
