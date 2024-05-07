package me.syncwrld.booter.minecraft.serialization;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import lombok.val;
import me.syncwrld.booter.Constants;
import org.bukkit.Bukkit;
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
    Map<String, Object> serializationMap = new HashMap<>();
    Map<Integer, String> items = new HashMap<>();

    int index = 0;
    for (ItemStack stack : content) {
      Map<String, Object> itemData = new HashMap<>();

      // Item properties
      val enchantments = stack.getEnchantments();
      int amount = stack.getAmount();
      val itemMeta = stack.getItemMeta();
      short durability = stack.getDurability();
      val data = stack.getData();

      if ((enchantments != null) && !enchantments.isEmpty())
        itemData.put("enchantments", gson.toJson(enchantments));
      if (stack.hasItemMeta()) itemData.put("meta", gson.toJson(itemMeta, ItemMeta.class));
      if (data != null) itemData.put("data", gson.toJson(data, MaterialData.class));


      itemData.put("amount", amount);
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
    Map<Integer, String> itemsMap =
        gson.fromJson(gson.toJson(deserializationMap.get("content")), HashMap.class);

    ItemStack[] contents = new ItemStack[inventoryType.getDefaultSize()];

    for (Map.Entry<Integer, String> entry : itemsMap.entrySet()) {
      int index = entry.getKey();
      String itemDataJson = entry.getValue();

      Map<String, Object> itemData = gson.fromJson(itemDataJson, HashMap.class);

      Map<Enchantment, Integer> enchantments =
          gson.fromJson((String) itemData.get("enchantments"), HashMap.class);
      int amount = (int) itemData.get("amount");
      ItemMeta itemMeta = gson.fromJson((String) itemData.get("meta"), ItemMeta.class);
      short durability = ((Number) itemData.get("durability")).shortValue();
      MaterialData data = gson.fromJson((String) itemData.get("data"), MaterialData.class);

      ItemStack itemStack = new ItemStack(data.getItemType(), amount, durability);

      if (itemMeta != null)
        itemStack.setItemMeta(itemMeta);

      itemStack.setData(data);

      if (enchantments != null)
        itemStack.addUnsafeEnchantments(enchantments);

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
      int amount = (int) itemData.get("amount");
      ItemMeta itemMeta = gson.fromJson((String) itemData.get("meta"), ItemMeta.class);
      short durability = ((Number) itemData.get("durability")).shortValue();
      MaterialData data = gson.fromJson((String) itemData.get("data"), MaterialData.class);

      ItemStack itemStack = new ItemStack(data.getItemType(), amount, durability);
      itemStack.setItemMeta(itemMeta);
      itemStack.setData(data);

      if (enchantments != null)
        itemStack.addUnsafeEnchantments(enchantments);

      inventory.setItem(index, itemStack);
    }

    return inventory;
  }
}
