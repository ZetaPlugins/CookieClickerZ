package com.zetaplugins.cookieclickerz.commands.MainCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import com.zetaplugins.cookieclickerz.CookieClickerZ;
import com.zetaplugins.cookieclickerz.util.achievements.AchievementType;
import com.zetaplugins.cookieclickerz.util.cookieevents.CookieEventType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainTabCompleter implements TabCompleter {
    public final CookieClickerZ plugin;

    public MainTabCompleter(CookieClickerZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) return getFirstArgOptions(sender, args);
        if (args.length == 2) return getSecondArgOptions(sender, args);
        if (args.length == 3) return getThirdArgOptions(sender, args);
        if (args.length == 4) return getFourthArgOptions(args);
        if (args.length == 5) return getFifthArgOptions(args);
        return List.of();
    }

    private List<String> getFirstArgOptions(CommandSender sender, String[] args) {
        String input = args[0].toLowerCase();

        List<String> options = new ArrayList<>();
        options.add("help");
        options.add("version");
        if (sender.hasPermission("cookieclickerz.admin.reload")) options.add("reload");
        if (sender.hasPermission("cookieclickerz.admin.manageclickers")) options.add("clicker");
        if (sender.hasPermission("cookieclickerz.admin.managecookies")) options.add("cookies");
        if (sender.hasPermission("cookieclickerz.admin.manageprestige")) options.add("prestige");
        if (sender.hasPermission("cookieclickerz.admin.manageevents")) options.add("events");
        if (sender.hasPermission("cookieclickerz.admin.manageachievements")) options.add("achievements");
        if (sender.hasPermission("cookieclickerz.numcheatsheet")) options.add("numbers");

        return getDisplayOptions(options, input);
    }

    private List<String> getSecondArgOptions(CommandSender sender, String[] args) {
        String input = args[1].toLowerCase();
        switch (args[0]) {
            case "help":
            case "version":
                return List.of();
            case "clicker":
                return getDisplayOptions(List.of("add", "remove", "list"), input);
            case "cookies":
            case "prestige":
            case "events":
            case "achievements":
                return null;
            case "dev":
                return getDisplayOptions(List.of("test", "addMockData", "savecached"), input);
            default:
                return List.of();
        }
    }

    private List<String> getThirdArgOptions(CommandSender sender, String[] args) {
        String input = args[2].toLowerCase();
        switch (args[0]) {
            case "clicker":
                if (args[1].equals("add")) return getDisplayOptions(List.of("<name>"), input);
                if (args[1].equals("remove")) return getDisplayOptions(plugin.getClickerManager().getClickerKeys(), input);
                return List.of();
            case "cookies":
                return getDisplayOptions(List.of("add", "remove", "set"), input);
            case "prestige":
            case "achievements":
                return getDisplayOptions(List.of("get", "set"), input);
            case "events":
                return getDisplayOptions(List.of("start", "get"), input);
            case "dev":
                if (args[1].equals("addMockData")) return getDisplayOptions(List.of("1", "2", "3", "4", "5"), input);
            default:
                return List.of();
        }
    }

    private List<String> getFourthArgOptions(String[] args) {
        String input = args[3].toLowerCase();
        switch (args[0]) {
            case "cookies":
                if (args[2].equals("add")) return getDisplayOptions(
                        List.of("100", "1K", "1M", "1B", "1T", "1Q", "1QQ", "1S", "1SS", "1O", "1N", "1D"),
                        input
                );
            case "prestige":
                if (args[2].equals("set")) return getDisplayOptions(List.of("0", "1", "2", "3", "4", "5"), input);
            case "events":
                if (args[2].equals("start")) return getDisplayOptions(getEventCompletion(), input);
            case "achievements":
                return getDisplayOptions(
                        AchievementType.getAll().stream().map(AchievementType::getSlug).collect(Collectors.toList()),
                        input
                );
            default:
                return List.of();
        }
    }

    private List<String> getFifthArgOptions(String[] args) {
        String input = args[4].toLowerCase();
        if (args[0].equals("achievements") && args[2].equals("set")) {
            AchievementType achievementType = AchievementType.getBySlug(args[3]).orElse(null);
            if (achievementType == null) return List.of();
            return List.of("0", String.valueOf(achievementType.getBigIntegerGoal()));
        }
        return List.of();
    }

    private List<String> getEventCompletion() {
        List<String> list = Arrays.stream(CookieEventType.values()).map(CookieEventType::name).collect(Collectors.toList());
        list.add("random");
        return list;
    }

    private static List<String> getDisplayOptions(List<String> options, String input) {
        return options.stream()
                .filter(option -> startsWithIgnoreCase(option, input))
                .collect(Collectors.toList());
    }

    private static boolean startsWithIgnoreCase(String str, String prefix) {
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
