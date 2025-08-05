package com.zetaplugins.cookieclickerz.util.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.MessageUtils;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CustomItem {
    private final ItemStack itemStack;

    public CustomItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public CustomItem(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public CustomItem() {
        this.itemStack = new ItemStack(Material.AIR);
    }

    public static CustomItem fromSkullBase64(String base64) throws MalformedURLException {
        return new CustomItem(SkullUtils.getSkullFromBase64(base64));
    }

    public static CustomItem fromSkullUrl(String url) throws MalformedURLException {
        return new CustomItem(SkullUtils.getSkullFromUrl(url));
    }

    public CustomItem setMaterial(Material material) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        int amount = itemStack.getAmount();
        ItemStack newItemStack = new ItemStack(material, amount);
        newItemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public CustomItem setName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(MessageUtils.formatMsg(name));
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem setName(Component name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(name);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem setCustomModelID(int customModelID) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(customModelID);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem setUnbreakable(boolean unbreakable) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(unbreakable);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem setEnchanted(boolean enchanted) {
        if (!enchanted) return this;

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public <P, C> CustomItem setCustomDataContainer(NamespacedKey key, PersistentDataType<P, C> persistentDataType, C value) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, persistentDataType, value);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public <P, C> CustomItem setCustomDataContainer(String key, PersistentDataType<P, C> persistentDataType, C value) {
        return setCustomDataContainer(new NamespacedKey(CookieClickerZ.getInstance(), key), persistentDataType, value);
    }

    public <P, C> C getCustomDataContainer(NamespacedKey key, PersistentDataType<P, C> persistentDataType) {
        return itemStack.getItemMeta().getPersistentDataContainer().get(key, persistentDataType);
    }

    public <P, C> C getCustomDataContainer(String key, PersistentDataType<P, C> persistentDataType) {
        return getCustomDataContainer(new NamespacedKey(CookieClickerZ.getInstance(), key), persistentDataType);
    }

    public CustomItem addFlag(ItemFlag itemFlag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(itemFlag);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem addEnchantment(Enchantment enchantment, int level) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public PersistentDataContainer getPersistentDataContainer() {
        return itemStack.getItemMeta().getPersistentDataContainer();
    }

    public CustomItem setLore(List<Component> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<Component> newLore = new ArrayList<>(lore.size());
        newLore.addAll(lore);
        itemMeta.lore(newLore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem addLore(String lore) {
        return addLore(MessageUtils.formatMsg(lore));
    }

    public CustomItem addLore(Component lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<Component> newLore = new ArrayList<>(Objects.requireNonNull(itemMeta.lore()));
        newLore.add(lore);
        itemMeta.lore(newLore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static ItemStack getHead(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        if (skullMeta != null && player != null && player.getName() != null) {
            skullMeta.setOwningPlayer(player);
            skullMeta.displayName(MessageUtils.formatMsg("&e" + player.getName()));
            head.setItemMeta(skullMeta);
        }

        return head;
    }

    public static ItemStack getHead(UUID uuid) {
        OfflinePlayer player = CookieClickerZ.getInstance().getServer().getOfflinePlayer(uuid);
        return getHead(player);
    }
}
