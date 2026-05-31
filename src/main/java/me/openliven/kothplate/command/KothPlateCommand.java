package me.openliven.kothplate.command;

import me.openliven.kothplate.KothPlatePlugin;
import me.openliven.kothplate.model.BlockPosition;
import me.openliven.kothplate.plate.PlatePlacementResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class KothPlateCommand implements TabExecutor {
    private final KothPlatePlugin plugin;

    public KothPlateCommand(KothPlatePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            plugin.messages().send(sender, "usage");
            return true;
        }

        if (!sender.hasPermission("koth.admin")) {
            plugin.messages().send(sender, "no-permission");
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "set" -> set(sender);
            case "reload" -> reload(sender);
            case "info" -> info(sender);
            default -> plugin.messages().send(sender, "usage");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1 || !sender.hasPermission("koth.admin")) {
            return Collections.emptyList();
        }

        List<String> values = List.of("set", "reload", "info");
        String prefix = args[0].toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String value : values) {
            if (value.startsWith(prefix)) {
                matches.add(value);
            }
        }
        return matches;
    }

    private void set(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            plugin.messages().send(sender, "only-player");
            return;
        }

        PlatePlacementResult result = plugin.plates().placeAtPlayerFeet(player, plugin.settings());
        if (result.status() == PlatePlacementResult.Status.BASE_NEEDED) {
            plugin.messages().send(sender, "base-needed");
            return;
        }
        if (result.status() == PlatePlacementResult.Status.BLOCKED) {
            plugin.messages().send(sender, "blocked");
            return;
        }

        savePlate(result.position());
        plugin.reloadPluginSettings();
        plugin.messages().send(sender, "point-established");
    }

    private void reload(CommandSender sender) {
        plugin.reloadPluginSettings();
        plugin.messages().send(sender, "config-reloaded");
    }

    private void info(CommandSender sender) {
        BlockPosition position = plugin.settings().platePosition();
        if (position == null) {
            plugin.messages().send(sender, "point-not-set");
            return;
        }

        plugin.messages().send(sender, "point-info",
                "%world%", position.worldName(),
                "%x%", Integer.toString(position.x()),
                "%y%", Integer.toString(position.y()),
                "%z%", Integer.toString(position.z()),
                "%time%", Integer.toString(plugin.settings().captureSeconds()),
                "%reward%", formatAmount(plugin.settings().rewardAmount()));
    }

    private void savePlate(BlockPosition position) {
        plugin.getConfig().set("zone.world", position.worldName());
        plugin.getConfig().set("zone.x", position.x());
        plugin.getConfig().set("zone.y", position.y());
        plugin.getConfig().set("zone.z", position.z());
        plugin.saveConfig();
    }

    private String formatAmount(double amount) {
        if (amount == Math.rint(amount)) {
            return Long.toString(Math.round(amount));
        }
        return Double.toString(amount);
    }
}
