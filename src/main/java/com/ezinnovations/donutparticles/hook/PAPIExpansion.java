package com.ezinnovations.donutparticles.hook;

import com.ezinnovations.donutparticles.DonutParticles;
import com.ezinnovations.donutparticles.PlayerSettings;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIExpansion extends PlaceholderExpansion {
    private final DonutParticles plugin;

    public PAPIExpansion(DonutParticles plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "donutparticles";
    }

    @Override
    public @NotNull String getAuthor() {
        return "EzInnovations";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || player.getUniqueId() == null) {
            return null;
        }

        PlayerSettings settings = plugin.getSettingsManager().getOrCreate(player.getUniqueId());
        return switch (params.toLowerCase()) {
            case "totem_particles" -> Boolean.toString(settings.isTotemParticles());
            case "explosion_particles" -> Boolean.toString(settings.isExplosionParticles());
            default -> null;
        };
    }
}
