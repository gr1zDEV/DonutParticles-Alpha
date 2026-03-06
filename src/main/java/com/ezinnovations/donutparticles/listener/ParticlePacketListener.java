package com.ezinnovations.donutparticles.listener;

import com.ezinnovations.donutparticles.DonutParticles;
import com.ezinnovations.donutparticles.PlayerSettings;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import org.bukkit.entity.Player;


public class ParticlePacketListener extends PacketListenerAbstract {
    private final DonutParticles plugin;

    public ParticlePacketListener(DonutParticles plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.PARTICLE) {
            return;
        }

        Player player = (Player) event.getPlayer();
        if (player == null) {
            return;
        }

        PlayerSettings settings = plugin.getSettingsManager().getOrCreate(player.getUniqueId());
        WrapperPlayServerParticle wrapper = new WrapperPlayServerParticle(event);
        Particle<?> particle = wrapper.getParticle();
        ParticleType<?> type = particle.getType();

        if (!settings.isExplosionParticles() && (type == ParticleTypes.EXPLOSION || type == ParticleTypes.EXPLOSION_EMITTER)) {
            event.setCancelled(true);
            return;
        }

        if (!settings.isTotemParticles() && type == ParticleTypes.TOTEM_OF_UNDYING) {
            event.setCancelled(true);
        }
    }
}
