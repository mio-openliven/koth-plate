package me.openliven.kothplate.config;

import me.openliven.kothplate.model.BlockPosition;
import me.openliven.kothplate.schedule.ScheduleSettings;
import me.openliven.kothplate.schedule.TimeWindow;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SettingsLoader {
    private final JavaPlugin plugin;

    public SettingsLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public PluginSettings load() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        String language = config.getString("settings.language", "ru");
        int captureSeconds = Math.max(1, config.getInt("settings.capture-time", 20));
        double rewardAmount = Math.max(0.0D, config.getDouble("settings.reward-amount", 25.0D));
        boolean respectCancelledPhysicalEvents = config.getBoolean("settings.respect-cancelled-physical-events", false);

        return new PluginSettings(
                language == null || language.isBlank() ? "ru" : language.toLowerCase(),
                captureSeconds,
                rewardAmount,
                respectCancelledPhysicalEvents,
                loadSchedule(config),
                loadPlatePosition(config)
        );
    }

    private ScheduleSettings loadSchedule(FileConfiguration config) {
        boolean enabled = config.getBoolean("settings.schedule.enabled", false);
        ZoneId zone = loadZone(config.getString("settings.schedule.timezone", ZoneId.systemDefault().getId()));
        List<TimeWindow> windows = new ArrayList<>();

        for (Map<?, ?> rawWindow : config.getMapList("settings.schedule.windows")) {
            Object start = rawWindow.get("start");
            Object end = rawWindow.get("end");
            if (start == null || end == null) {
                plugin.getLogger().warning("Skipping schedule window without start/end.");
                continue;
            }

            try {
                windows.add(new TimeWindow(LocalTime.parse(start.toString()), LocalTime.parse(end.toString())));
            } catch (DateTimeException exception) {
                plugin.getLogger().warning("Skipping invalid schedule window: " + start + " - " + end);
            }
        }

        return new ScheduleSettings(enabled, zone, List.copyOf(windows));
    }

    private ZoneId loadZone(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (DateTimeException exception) {
            plugin.getLogger().warning("Invalid schedule.timezone '" + timezone + "'. Falling back to server timezone.");
            return ZoneId.systemDefault();
        }
    }

    private BlockPosition loadPlatePosition(FileConfiguration config) {
        if (!config.isConfigurationSection("zone")) {
            return null;
        }

        String worldName = config.getString("zone.world");
        if (worldName == null || worldName.isBlank()) {
            return null;
        }

        return new BlockPosition(
                worldName,
                config.getInt("zone.x"),
                config.getInt("zone.y"),
                config.getInt("zone.z")
        );
    }
}
