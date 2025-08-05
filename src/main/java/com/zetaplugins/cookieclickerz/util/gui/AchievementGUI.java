package com.zetaplugins.cookieclickerz.util.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.LanguageManager;
import com.zetaplugins.cookieclickerz.util.MessageUtils;
import com.zetaplugins.cookieclickerz.util.NumFormatter;
import com.zetaplugins.cookieclickerz.util.achievements.Achievement;
import com.zetaplugins.cookieclickerz.util.achievements.AchievementCategory;
import com.zetaplugins.cookieclickerz.util.achievements.AchievementType;
import com.zetaplugins.cookieclickerz.util.items.CustomItem;
import com.zetaplugins.cookieclickerz.storage.PlayerData;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AchievementGUI {
    private static final List<UUID> openInventories = new ArrayList<>();

    private AchievementGUI() {}

    public static boolean isOpen(Player player) {
        return openInventories.contains(player.getUniqueId());
    }

    public static void open(Player player) {
        final CookieClickerZ plugin = CookieClickerZ.getInstance();
        PlayerData playerData = plugin.getStorage().load(player.getUniqueId());

        Inventory inventory = Bukkit.createInventory(
                null,
                6 * 9,
                MessageUtils.getAndFormatMsg(
                        false,
                        "inventories.achievements.title",
                        "&8Achievements"
                )
        );

        GuiAssets.fillInventoryWithGlass(inventory);

        inventory.setItem(0, getAchievementCategoryHead(AchievementCategory.CLICKS));
        inventory.setItem(9, getAchievementItem(playerData, AchievementType.CLICKER_ROOKIE));
        inventory.setItem(18, getAchievementItem(playerData, AchievementType.FINGER_WORKOUT));
        inventory.setItem(27, getAchievementItem(playerData, AchievementType.CLICK_CHAMPION));
        inventory.setItem(36, getAchievementItem(playerData, AchievementType.UNSTOPPABLE_CLICKER));
        inventory.setItem(45, getAchievementItem(playerData, AchievementType.COOKIE_MACHINE));

        inventory.setItem(2, getAchievementCategoryHead(AchievementCategory.COOKIES));
        inventory.setItem(11, getAchievementItem(playerData, AchievementType.FIRST_BATCH));
        inventory.setItem(20, getAchievementItem(playerData, AchievementType.COOKIE_CONNOISSEUR));
        inventory.setItem(29, getAchievementItem(playerData, AchievementType.COOKIE_COLLECTOR));
        inventory.setItem(38, getAchievementItem(playerData, AchievementType.COOKIE_HOARDER));
        inventory.setItem(47, getAchievementItem(playerData, AchievementType.COOKIE_OVERLORD));

        inventory.setItem(4, getAchievementCategoryHead(AchievementCategory.UPGRADES));
        inventory.setItem(13, getAchievementItem(playerData, AchievementType.SMART_SHOPPER));
        inventory.setItem(22, getAchievementItem(playerData, AchievementType.UPGRADE_MASTER));
        inventory.setItem(31, getAchievementItem(playerData, AchievementType.SAVVY_SPENDER));
        inventory.setItem(40, getAchievementItem(playerData, AchievementType.UPGRADE_ENTHUSIAST));
        inventory.setItem(49, getAchievementItem(playerData, AchievementType.GOTTA_UPGRADE_THEM_ALL));

        inventory.setItem(6, getAchievementCategoryHead(AchievementCategory.PRESTIGE));
        inventory.setItem(15, getAchievementItem(playerData, AchievementType.REBAKED_AND_READY));
        inventory.setItem(24, getAchievementItem(playerData, AchievementType.TWICE_AS_TASTY));
        inventory.setItem(33, getAchievementItem(playerData, AchievementType.THIRD_TIMES_A_CHARM));
        inventory.setItem(42, getAchievementItem(playerData, AchievementType.OOPS_I_DID_IT_AGAIN));
        inventory.setItem(51, getAchievementItem(playerData, AchievementType.OVEN_ETERNAL));

        inventory.setItem(8, getAchievementCategoryHead(AchievementCategory.EVENTS));
        inventory.setItem(17, getAchievementItem(playerData, AchievementType.EVENT_HORIZON));
        inventory.setItem(26, getAchievementItem(playerData, AchievementType.NOT_MY_COOKIES));
        inventory.setItem(35, getAchievementItem(playerData, AchievementType.SUGAR_RUSH));
        inventory.setItem(44, getAchievementItem(playerData, AchievementType.MONEY_MAGNET));
        inventory.setItem(53, getAchievementItem(playerData, AchievementType.OOF));

        player.openInventory(inventory);
        openInventories.add(player.getUniqueId());
    }

    private static ItemStack getAchievementItem(PlayerData playerData, AchievementType achievementType) {
        Optional<Achievement> achievementOptional = playerData.getAchievement(achievementType);
        boolean isCompleted = false;
        String progess = "0";
        String goal = achievementType.getCategory() == AchievementCategory.COOKIES
                ? NumFormatter.formatBigInt(achievementType.getBigIntegerGoal())
                : achievementType.getGoal() + "";

        if (achievementOptional.isPresent()) {
            Achievement achievement = achievementOptional.get();
            isCompleted = achievement.isCompleted();
            progess = achievement.getProgress() + "";
            if (achievement.getType().getCategory().equals(AchievementCategory.COOKIES)) {
                progess =
                        playerData.getTotalCookies().compareTo(achievement.getType().getBigIntegerGoal()) >= 0
                                ? NumFormatter.formatBigInt(achievement.getType().getBigIntegerGoal())
                                : NumFormatter.formatBigInt(playerData.getTotalCookies());
                goal = NumFormatter.formatBigInt(achievement.getType().getBigIntegerGoal());
            }
        }

        LanguageManager languageManager = CookieClickerZ.getInstance().getLanguageManager();

        String achievementName = languageManager.getString("achievements." + achievementType.getSlug() + ".name");
        String achievementDescription = languageManager.getString("achievements." + achievementType.getSlug() + ".description");
        String achievementCompleted = isCompleted
                ? languageManager.getString("inventories.achievements.completed")
                : languageManager.getString("inventories.achievements.notCompleted");

        return new CustomItem(isCompleted ? Material.LIME_DYE : Material.GRAY_DYE)
                .setName(MessageUtils.getAndFormatMsg(
                        false,
                        "inventories.achievements.achievementTitle",
                        "<#9932cc>%title%",
                        new MessageUtils.Replaceable<>("%title%", achievementName),
                        new MessageUtils.Replaceable<>("%completed%", achievementCompleted)
                ))
                .setLore(MessageUtils.getAndFormatMsgList(
                        "inventories.achievements.achievementDescription",
                        new MessageUtils.Replaceable<>("%description%", achievementDescription),
                        new MessageUtils.Replaceable<>("%progress%", progess),
                        new MessageUtils.Replaceable<>("%goal%", goal),
                        new MessageUtils.Replaceable<>("%completed%", achievementCompleted)
                ))
                .getItemStack();
    }

    private static ItemStack getAchievementCategoryHead(AchievementCategory category) {
        String name = CookieClickerZ.getInstance().getLanguageManager().getString("achievementCategories." + category.getSlug());
        if (name == null) name = category.getSlug();

        try {
            return CustomItem.fromSkullBase64(category.getHeadBase64())
                    .setName(name)
                    .getItemStack();
        } catch (MalformedURLException e) {
            return new CustomItem(Material.SKELETON_SKULL)
                    .setName(name)
                    .getItemStack();
        }
    }

    public static void close(Player player) {
        if (isOpen(player)) openInventories.remove(player.getUniqueId());
    }
}
