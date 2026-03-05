package com.ezinnovations.donutparticles.listener;

import com.ezinnovations.donutparticles.DonutParticles;
import com.ezinnovations.donutparticles.PlayerSettings;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ParticlePacketListener extends PacketListenerAbstract {
    private final DonutParticles plugin;

    public ParticlePacketListener(DonutParticles plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.WORLD_PARTICLES) {
            return;
        }

        UUID playerUuid = event.getUser().getUUID();
        if (playerUuid == null) {
            return;
        }

        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) {
            return;
        }

        PlayerSettings settings = plugin.getSettingsManager().getOrCreate(playerUuid);
        WrapperPlayServerParticle wrapper = new WrapperPlayServerParticle(event);
        ParticleType<?> type = wrapper.getParticle().getType();

        if (!settings.isExplosionParticles() && (type == ParticleTypes.EXPLOSION || type == ParticleTypes.EXPLOSION_EMITTER)) {
            event.setCancelled(true);
            return;
        }

        if (!settings.isTotemParticles() && type == ParticleTypes.TOTEM_OF_UNDYING) {
            event.setCancelled(true);
        }
    }
}
