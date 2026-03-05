package com.ezinnovations.donutparticles;

public class PlayerSettings {
    private boolean explosionParticles;
    private boolean totemParticles;

    public PlayerSettings(boolean explosionParticles, boolean totemParticles) {
        this.explosionParticles = explosionParticles;
        this.totemParticles = totemParticles;
    }

    public boolean isExplosionParticles() {
        return explosionParticles;
    }

    public void setExplosionParticles(boolean explosionParticles) {
        this.explosionParticles = explosionParticles;
    }

    public boolean isTotemParticles() {
        return totemParticles;
    }

    public void setTotemParticles(boolean totemParticles) {
        this.totemParticles = totemParticles;
    }
}
