package me.openliven.kothplate.config;

import org.bukkit.Particle;

public record VisualEffectSettings(
        boolean enabled,
        Particle particle,
        int count,
        double offsetX,
        double offsetY,
        double offsetZ,
        double speed
) {
}
