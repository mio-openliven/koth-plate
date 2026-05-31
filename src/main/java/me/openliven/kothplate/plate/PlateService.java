package me.openliven.kothplate.plate;

import me.openliven.kothplate.config.PluginSettings;
import me.openliven.kothplate.model.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlateService {
    private final JavaPlugin plugin;
    public static final Material CAPTURE_PLATE = Material.HEAVY_WEIGHTED_PRESSURE_PLATE;

    public PlateService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public PlatePlacementResult placeAtPlayerFeet(Player player, PluginSettings settings) {
        Block baseBlock = player.getLocation().clone().subtract(0.0D, 0.1D, 0.0D).getBlock();
        if (!baseBlock.getType().isSolid()) {
            return new PlatePlacementResult(PlatePlacementResult.Status.BASE_NEEDED, null);
        }

        Block targetBlock = baseBlock.getRelative(BlockFace.UP);
        if (!targetBlock.getType().isAir() && !isPressurePlate(targetBlock.getType())) {
            return new PlatePlacementResult(PlatePlacementResult.Status.BLOCKED, null);
        }

        targetBlock.setType(CAPTURE_PLATE);
        return new PlatePlacementResult(PlatePlacementResult.Status.SUCCESS, BlockPosition.fromBlock(targetBlock));
    }

    public void clear(BlockPosition position) {
        if (position == null) {
            return;
        }

        Location location = position.toLocation();
        if (location == null) {
            plugin.getLogger().warning("Cannot clear plate because world is not loaded: " + position.worldName());
            return;
        }

        Block block = location.getBlock();
        if (isPressurePlate(block.getType())) {
            block.setType(Material.AIR);
        }
    }

    public Player findPlayerOnPlate(BlockPosition position) {
        if (position == null) {
            return null;
        }

        Location location = position.toLocation();
        if (location == null) {
            return null;
        }

        World world = location.getWorld();
        if (world == null) {
            return null;
        }

        for (Player player : world.getPlayers()) {
            if (isStandingOnPlate(player, position)) {
                return player;
            }
        }
        return null;
    }

    public boolean isCapturePlate(Block block, BlockPosition position) {
        return position != null
                && block.getType() == CAPTURE_PLATE
                && position.matches(block);
    }

    public boolean isStandingOnPlate(Player player, BlockPosition position) {
        if (position == null) {
            return false;
        }
        Block feetBlock = player.getLocation().getBlock();
        Block slightlyBelowFeet = player.getLocation().clone().subtract(0.0D, 0.1D, 0.0D).getBlock();
        return position.matches(feetBlock) || position.matches(slightlyBelowFeet);
    }

    private boolean isPressurePlate(Material material) {
        return material.name().endsWith("_PRESSURE_PLATE");
    }
}
