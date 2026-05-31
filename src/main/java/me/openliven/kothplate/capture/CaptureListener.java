package me.openliven.kothplate.capture;

import me.openliven.kothplate.KothPlatePlugin;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class CaptureListener implements Listener {
    private final KothPlatePlugin plugin;
    private final CaptureService captures;

    public CaptureListener(KothPlatePlugin plugin, CaptureService captures) {
        this.plugin = plugin;
        this.captures = captures;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPhysicalInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        if (event.useInteractedBlock() == Event.Result.DENY && plugin.settings().respectCancelledPhysicalEvents()) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !plugin.plates().isCapturePlate(clickedBlock, plugin.settings().platePosition())) {
            return;
        }

        captures.tryStart(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        captures.cancelIfActive(event.getPlayer(), false);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        captures.cancelIfActive(event.getEntity(), false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() == GameMode.CREATIVE || event.getNewGameMode() == GameMode.SPECTATOR) {
            captures.cancelIfActive(event.getPlayer().getUniqueId(), true);
        }
    }
}
