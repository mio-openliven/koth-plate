package me.openliven.kothplate.service;

import me.openliven.kothplate.config.AfterRewardMode;
import me.openliven.kothplate.config.AfterRewardSettings;
import me.openliven.kothplate.model.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class AfterRewardActionService {
    private static final double MIN_DIRECTION_LENGTH_SQUARED = 0.0001D;

    public void apply(Player player, AfterRewardSettings settings, BlockPosition platePosition) {
        if (!settings.enabled() || platePosition == null || !player.isOnline()) {
            return;
        }

        AfterRewardMode mode = settings.mode();
        Location plateCenter = plateCenter(player, platePosition);
        if (mode.airBurst()) {
            playAirBurst(plateCenter);
        }
        if (mode.movesWinner()) {
            pushSideways(player, plateCenter, mode.horizontalStrength());
        }
    }

    private void playAirBurst(Location plateCenter) {
        World world = plateCenter.getWorld();
        if (world == null) {
            return;
        }

        world.spawnParticle(Particle.CLOUD, plateCenter, 14, 0.35D, 0.08D, 0.35D, 0.03D);
        world.playSound(plateCenter, Sound.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.4F);
    }

    private void pushSideways(Player player, Location plateCenter, double strength) {
        Vector direction = directionAwayFromPlate(player, plateCenter);
        Vector velocity = direction.multiply(strength);
        velocity.setY(Math.min(0.0D, player.getVelocity().getY()));
        player.setFallDistance(0.0F);
        player.setVelocity(velocity);
    }

    private Location plateCenter(Player player, BlockPosition platePosition) {
        return new Location(
                player.getWorld(),
                platePosition.x() + 0.5D,
                platePosition.y() + 0.2D,
                platePosition.z() + 0.5D
        );
    }

    private Vector directionAwayFromPlate(Player player, Location plateCenter) {
        Vector direction = player.getLocation().toVector().subtract(plateCenter.toVector()).setY(0.0D);
        if (direction.lengthSquared() >= MIN_DIRECTION_LENGTH_SQUARED) {
            return direction.normalize();
        }

        Vector facing = player.getLocation().getDirection().setY(0.0D);
        if (facing.lengthSquared() >= MIN_DIRECTION_LENGTH_SQUARED) {
            return facing.normalize();
        }

        return new Vector(1.0D, 0.0D, 0.0D);
    }
}
