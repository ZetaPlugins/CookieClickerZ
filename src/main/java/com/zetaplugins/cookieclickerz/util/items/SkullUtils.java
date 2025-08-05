package com.zetaplugins.cookieclickerz.util.items;

import org.bukkit.Bukkit;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public final class SkullUtils {
    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4");

    private SkullUtils() {}

    private static PlayerProfile getProfile(String url) throws MalformedURLException {
        PlayerProfile profile = Bukkit.createProfile(RANDOM_UUID);
        PlayerTextures textures = profile.getTextures();
        URL urlObject = new URL(url);
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
    }

    public static URL getUrlFromBase64(String base64) throws MalformedURLException {
        String decoded = new String(Base64.getDecoder().decode(base64));
        return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
    }

    public static ItemStack getSkullFromUrl(String url) throws MalformedURLException {
        PlayerProfile profile = getProfile(url);
        ItemStack skull = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setPlayerProfile(profile);
            skull.setItemMeta(skullMeta);
        }
        return skull;
    }

    public static ItemStack getSkullFromBase64(String base64) throws MalformedURLException {
        URL url = getUrlFromBase64(base64);
        return getSkullFromUrl(url.toString());
    }
}
