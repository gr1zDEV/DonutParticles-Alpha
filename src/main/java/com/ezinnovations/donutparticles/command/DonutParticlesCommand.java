package com.ezinnovations.donutparticles.command;

import com.ezinnovations.donutparticles.DonutParticles;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DonutParticlesCommand implements CommandExecutor, TabCompleter {
    private final DonutParticles plugin;

    public DonutParticlesCommand(DonutParticles plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("donutparticles.admin")) {
            plugin.sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPluginConfig();
            plugin.getSettingsManager().reload();
            plugin.sendMessage(sender, "reload-success");
            return true;
        }

        if (args.length != 3) {
            return false;
        }

        String category = args[0].toLowerCase(Locale.ROOT);
        String state = args[1].toLowerCase(Locale.ROOT);
        String targetName = args[2];

        if ((!category.equals("explode") && !category.equals("totem")) || (!state.equals("on") && !state.equals("off"))) {
            return false;
        }

        UUID targetUuid;
        String displayName;

        Player online = Bukkit.getPlayerExact(targetName);
        if (online != null) {
            targetUuid = online.getUniqueId();
            displayName = online.getName();
        } else {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(targetName);
            if ((!offline.hasPlayedBefore() && !offline.isOnline()) || offline.getUniqueId() == null) {
                plugin.sendMessage(sender, "player-not-found", targetName);
                return true;
            }
            targetUuid = offline.getUniqueId();
            displayName = offline.getName() != null ? offline.getName() : targetName;
        }

        boolean enabled = state.equals("on");
        if (category.equals("explode")) {
            plugin.getSettingsManager().setExplosion(targetUuid, enabled);
            plugin.sendMessage(sender, enabled ? "admin-explosion-enabled" : "admin-explosion-disabled", displayName);
        } else {
            plugin.getSettingsManager().setTotem(targetUuid, enabled);
            plugin.sendMessage(sender, enabled ? "admin-totem-enabled" : "admin-totem-disabled", displayName);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("donutparticles.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return filter(args[0], List.of("explode", "totem", "reload"));
        }

        if (args.length == 2 && !args[0].equalsIgnoreCase("reload")) {
            return filter(args[1], List.of("on", "off"));
        }

        if (args.length == 3 && !args[0].equalsIgnoreCase("reload")) {
            List<String> players = new ArrayList<>();
            for (Player online : Bukkit.getOnlinePlayers()) {
                players.add(online.getName());
            }
            return filter(args[2], players);
        }

        return Collections.emptyList();
    }

    private List<String> filter(String input, List<String> values) {
        String lower = input.toLowerCase(Locale.ROOT);
        return values.stream().filter(entry -> entry.toLowerCase(Locale.ROOT).startsWith(lower)).toList();
    }
}
