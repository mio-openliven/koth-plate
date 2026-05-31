package me.openliven.kothplate.plate;

import me.openliven.kothplate.config.PluginSettings;
import me.openliven.kothplate.model.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public final class PlateService {
    public static final Material CAPTURE_PLATE = Material.HEAVY_WEIGHTED_PRESSURE_PLATE;

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

    public PlateDiagnostic diagnose(BlockPosition position) {
        if (position == null) {
            return new PlateDiagnostic(PlateDiagnostic.Status.NOT_CONFIGURED, "");
        }

        World world = Bukkit.getWorld(position.worldName());
        if (world == null) {
            return new PlateDiagnostic(PlateDiagnostic.Status.WORLD_NOT_LOADED, "");
        }

        int chunkX = position.x() >> 4;
        int chunkZ = position.z() >> 4;
        if (!world.isChunkLoaded(chunkX, chunkZ)) {
            return new PlateDiagnostic(PlateDiagnostic.Status.CHUNK_NOT_LOADED, "");
        }

        Location location = new Location(world, position.x(), position.y(), position.z());
        Material material = location.getBlock().getType();
        if (material == CAPTURE_PLATE) {
            return new PlateDiagnostic(PlateDiagnostic.Status.READY, material.name());
        }
        return new PlateDiagnostic(PlateDiagnostic.Status.WRONG_BLOCK, material.name());
    }

    private boolean isPressurePlate(Material material) {
        return material.name().endsWith("_PRESSURE_PLATE");
    }
}
