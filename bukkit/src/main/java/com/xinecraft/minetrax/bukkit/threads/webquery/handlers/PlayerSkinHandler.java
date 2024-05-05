package com.xinecraft.minetrax.bukkit.threads.webquery.handlers;

import com.xinecraft.minetrax.bukkit.MinetraxBukkit;
import com.xinecraft.minetrax.common.utils.LoggingUtil;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class PlayerSkinHandler {
    public static String setPlayerSkinUsingUrlOrName(String playerUuid, String value) {
        try {
            SkinsRestorer skinsRestorerApi = MinetraxBukkit.getPlugin().getSkinsRestorerApi();
            SkinStorage skinStorage = skinsRestorerApi.getSkinStorage();
            PlayerStorage playerStorage = skinsRestorerApi.getPlayerStorage();
            Optional<InputDataResult> result = skinStorage.findOrCreateSkinData(value);
            if (!result.isPresent()) {
                return null;
            }

            // Assign the skin to player.
            playerStorage.setSkinIdOfPlayer(UUID.fromString(playerUuid), result.get().getIdentifier());

            // Instantly apply skin to the player without requiring the player to rejoin, if online
            Player player = Bukkit.getPlayer(UUID.fromString(playerUuid));
            if (player != null) {
                skinsRestorerApi.getSkinApplier(Player.class).applySkin(player);
            }

            return "ok";
        } catch (Exception e) {
            LoggingUtil.warning(e.getMessage());
            return null;
        }
    }

    public static String initSetPlayerSkinUsingCustom(String playerUuid, String value) {
        // Store the skin data in a hashmap for given player uuid key
        try {
            MinetraxBukkit.getPlugin().skinRestorerValueCache.put(playerUuid, value);
            return "ok";
        } catch (Exception e) {
            LoggingUtil.warning(e.getMessage());
            return null;
        }
    }

    public static String setPlayerSkinUsingCustom(String playerUuid, String skinSignature) {
        try {
            SkinsRestorer skinsRestorerApi = MinetraxBukkit.getPlugin().getSkinsRestorerApi();
            SkinStorage skinStorage = skinsRestorerApi.getSkinStorage();
            PlayerStorage playerStorage = skinsRestorerApi.getPlayerStorage();

            // Find the skin value from cache
            String skinValue = MinetraxBukkit.getPlugin().skinRestorerValueCache.get(playerUuid);
            // Error if skin value is null
            if (skinValue == null) {
                return null;
            }

            skinStorage.setCustomSkinData(playerUuid, SkinProperty.of(skinValue, skinSignature));
            Optional<InputDataResult> result = skinStorage.findSkinData(playerUuid);
            if (!result.isPresent()) {
                return null;
            }

            // Assign the skin to player.
            playerStorage.setSkinIdOfPlayer(UUID.fromString(playerUuid), result.get().getIdentifier());

            // Instantly apply skin to the player without requiring the player to rejoin, if online
            Player player = Bukkit.getPlayer(UUID.fromString(playerUuid));
            if (player != null) {
                skinsRestorerApi.getSkinApplier(Player.class).applySkin(player);
            }

            // Delete the skin value from cache
            MinetraxBukkit.getPlugin().skinRestorerValueCache.remove(playerUuid);

            return "ok";
        } catch (Exception e) {
            LoggingUtil.warning(e.getMessage());
            return null;
        }
    }

    public static String clearPlayerSkin(String playerUuid) {
        try {
            SkinsRestorer skinsRestorerApi = MinetraxBukkit.getPlugin().getSkinsRestorerApi();
            PlayerStorage playerStorage = skinsRestorerApi.getPlayerStorage();
            playerStorage.removeSkinIdOfPlayer(UUID.fromString(playerUuid));

            // Instantly apply skin to the player without requiring the player to rejoin, if online
            Player player = Bukkit.getPlayer(UUID.fromString(playerUuid));
            if (player != null) {
                skinsRestorerApi.getSkinApplier(Player.class).applySkin(player);
            }

            return "ok";
        } catch (Exception e) {
            LoggingUtil.warning(e.getMessage());
            return null;
        }
    }
}
