package com.ezinnovations.donutparticles;

import com.ezinnovations.donutparticles.command.DonutParticlesCommand;
import com.ezinnovations.donutparticles.command.ExplodeCommand;
import com.ezinnovations.donutparticles.command.TotemCommand;
import com.ezinnovations.donutparticles.hook.PAPIExpansion;
import com.ezinnovations.donutparticles.listener.ParticlePacketListener;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.jetbrains.annotations.NotNull;

public class DonutParticles extends JavaPlugin implements Listener {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private SettingsManager settingsManager;
    private GlobalRegionScheduler globalScheduler;

    private boolean defaultExplosionParticles;
    private boolean defaultTotemParticles;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPluginConfig();

        this.settingsManager = new SettingsManager(this);
        this.settingsManager.load();

        PacketEvents.getAPI().load();
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new ParticlePacketListener(this));

        DonutParticlesCommand adminCommand = new DonutParticlesCommand(this);
        getCommand("explode").setExecutor(new ExplodeCommand(this));
        getCommand("explode").setTabCompleter(new ExplodeCommand(this));
        getCommand("totem").setExecutor(new TotemCommand(this));
        getCommand("totem").setTabCompleter(new TotemCommand(this));
        getCommand("donutparticles").setExecutor(adminCommand);
        getCommand("donutparticles").setTabCompleter(adminCommand);

        Bukkit.getPluginManager().registerEvents(this, this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPIExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered.");
        }

        this.globalScheduler = Bukkit.getGlobalRegionScheduler();
        startAutoSave();
    }

    @Override
    public void onDisable() {
        if (settingsManager != null) {
            settingsManager.save();
        }
        if (PacketEvents.getAPI() != null) {
            PacketEvents.getAPI().terminate();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        settingsManager.getOrCreate(player.getUniqueId());
    }

    public void startAutoSave() {
        int interval = Math.max(30, getConfig().getInt("auto-save-interval", 300));
        long ticks = interval * 20L;
        globalScheduler.runAtFixedRate(this, task -> settingsManager.save(), ticks, ticks);
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.defaultExplosionParticles = getConfig().getBoolean("defaults.explosion-particles", true);
        this.defaultTotemParticles = getConfig().getBoolean("defaults.totem-particles", true);
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public boolean getDefaultExplosionParticles() {
        return defaultExplosionParticles;
    }

    public boolean getDefaultTotemParticles() {
        return defaultTotemParticles;
    }

    public void sendMessage(CommandSender sender, String path) {
        String message = getConfig().getString("messages." + path, "<red>Missing message: " + path);
        sender.sendMessage(miniMessage.deserialize(message));
    }

    public void sendMessage(CommandSender sender, String path, String player) {
        String message = getConfig().getString("messages." + path, "<red>Missing message: " + path)
                .replace("{player}", player);
        sender.sendMessage(miniMessage.deserialize(message));
    }

    public @NotNull PlayerSettings getSettings(Player player) {
        return settingsManager.getOrCreate(player.getUniqueId());
    }
}
