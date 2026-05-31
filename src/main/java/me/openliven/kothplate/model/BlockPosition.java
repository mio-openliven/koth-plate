package me.openliven.kothplate.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public record BlockPosition(String worldName, int x, int y, int z) {
    public static BlockPosition fromBlock(Block block) {
        return new BlockPosition(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    public Location toLocation() {
        World world = Bukkit.getWorld(worldName);
        return world == null ? null : new Location(world, x, y, z);
    }

    public boolean matches(Block block) {
        return block.getWorld().getName().equals(worldName)
                && block.getX() == x
                && block.getY() == y
                && block.getZ() == z;
    }
}
