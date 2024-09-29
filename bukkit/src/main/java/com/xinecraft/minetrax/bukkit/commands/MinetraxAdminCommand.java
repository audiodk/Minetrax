package com.xinecraft.minetrax.bukkit.commands;

import com.xinecraft.minetrax.common.MinetraxCommon;
import com.xinecraft.minetrax.common.enums.BanWardenSyncType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MinetraxAdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            usage(commandSender);
            return true;
        }

        String firstArg = strings[0];
        if (firstArg.equalsIgnoreCase("help") || firstArg.equalsIgnoreCase("?")) {
            usage(commandSender);
            return true;
        }

        if (firstArg.equalsIgnoreCase("banwarden:sync")) {
            String secondArg = strings.length > 1 ? strings[1].toLowerCase() : "all";
            commandSender.sendMessage(ChatColor.GREEN + "[BanWarden] Syncing " + secondArg + " ban data to web, plz check server logs for progress...");
            banwardenSyncBans(secondArg);
            return true;
        }

        return false;
    }

    private void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "Minetrax Admin Commands:");
        sender.sendMessage(ChatColor.GREEN + "/minetrax banwarden:sync");
        sender.sendMessage(ChatColor.GRAY + "   Sync bans from ban plugin to minetrax website.");
        sender.sendMessage(ChatColor.GREEN + "/minetrax help");
        sender.sendMessage(ChatColor.GRAY + "   Shows help message.");
    }

    private void banwardenSyncBans(String typeString) {
        BanWardenSyncType syncType = switch (typeString) {
            case "active" -> BanWardenSyncType.ACTIVE;
            case "expired" -> BanWardenSyncType.EXPIRED;
            default -> BanWardenSyncType.ALL;
        };
        MinetraxCommon.getInstance().getBanWarden().sync(syncType);
        return;
    }
}
