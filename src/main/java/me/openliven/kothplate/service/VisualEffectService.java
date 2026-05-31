package me.openliven.kothplate.service;

import me.openliven.kothplate.config.VisualEffectSettings;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public final class VisualEffectService {
    private final Logger logger;

    public VisualEffectService(Logger logger) {
        this.logger = logger;
    }

    public void play(Player player, VisualEffectSettings settings) {
        if (!settings.enabled() || settings.count() <= 0) {
            return;
        }

        Location location = player.getLocation().add(0.0D, 0.9D, 0.0D);
        try {
            player.getWorld().spawnParticle(
                    settings.particle(),
                    location,
                    settings.count(),
                    settings.offsetX(),
                    settings.offsetY(),
                    settings.offsetZ(),
                    settings.speed()
            );
        } catch (RuntimeException exception) {
            logger.warning("Cannot play KoTH particle " + settings.particle().name() + ": " + exception.getMessage());
        }
    }
}
