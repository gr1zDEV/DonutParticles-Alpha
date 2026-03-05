package com.ezinnovations.donutparticles.command;

import com.ezinnovations.donutparticles.DonutParticles;
import com.ezinnovations.donutparticles.PlayerSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ExplodeCommand implements CommandExecutor, TabCompleter {
    private final DonutParticles plugin;

    public ExplodeCommand(DonutParticles plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.sendMessage(sender, "player-only");
            return true;
        }

        if (!player.hasPermission("donutparticles.explode.toggle")) {
            plugin.sendMessage(player, "no-permission");
            return true;
        }

        if (args.length != 1 || !args[0].equalsIgnoreCase("toggle")) {
            return false;
        }

        PlayerSettings settings = plugin.getSettings(player);
        settings.setExplosionParticles(!settings.isExplosionParticles());
        plugin.sendMessage(player, settings.isExplosionParticles() ? "explosion-enabled" : "explosion-disabled");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("toggle");
        }
        return Collections.emptyList();
    }
}
