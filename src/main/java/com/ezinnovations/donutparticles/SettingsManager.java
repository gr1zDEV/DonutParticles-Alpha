package com.ezinnovations.donutparticles;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SettingsManager {
    private final DonutParticles plugin;
    private final Map<UUID, PlayerSettings> settingsMap = new ConcurrentHashMap<>();
    private File playerDataFile;
    private YamlConfiguration playerData;

    public SettingsManager(DonutParticles plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            try {
                if (playerDataFile.getParentFile() != null) {
                    playerDataFile.getParentFile().mkdirs();
                }
                playerDataFile.createNewFile();
            } catch (IOException exception) {
                plugin.getLogger().severe("Failed to create playerdata.yml: " + exception.getMessage());
            }
        }

        this.playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        this.settingsMap.clear();

        if (playerData.isConfigurationSection("players")) {
            for (String key : playerData.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    boolean explosion = playerData.getBoolean("players." + key + ".explosion-particles", plugin.getDefaultExplosionParticles());
                    boolean totem = playerData.getBoolean("players." + key + ".totem-particles", plugin.getDefaultTotemParticles());
                    settingsMap.put(uuid, new PlayerSettings(explosion, totem));
                } catch (IllegalArgumentException ignored) {
                    plugin.getLogger().warning("Skipping invalid UUID entry in playerdata.yml: " + key);
                }
            }
        }
    }

    public void save() {
        if (playerData == null || playerDataFile == null) {
            return;
        }

        playerData.set("players", null);
        for (Map.Entry<UUID, PlayerSettings> entry : settingsMap.entrySet()) {
            String base = "players." + entry.getKey();
            playerData.set(base + ".explosion-particles", entry.getValue().isExplosionParticles());
            playerData.set(base + ".totem-particles", entry.getValue().isTotemParticles());
        }

        try {
            playerData.save(playerDataFile);
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to save playerdata.yml: " + exception.getMessage());
        }
    }

    public void reload() {
        load();
    }

    public PlayerSettings getOrCreate(UUID uuid) {
        return settingsMap.computeIfAbsent(uuid,
                ignored -> new PlayerSettings(plugin.getDefaultExplosionParticles(), plugin.getDefaultTotemParticles()));
    }

    public void setExplosion(UUID uuid, boolean value) {
        getOrCreate(uuid).setExplosionParticles(value);
    }

    public void setTotem(UUID uuid, boolean value) {
        getOrCreate(uuid).setTotemParticles(value);
    }
}
