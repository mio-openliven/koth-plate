package me.openliven.kothplate.config;

import me.openliven.kothplate.model.BlockPosition;
import me.openliven.kothplate.schedule.ScheduleSettings;
import me.openliven.kothplate.schedule.TimeWindow;
import org.bukkit.configuration.ConfigurationSection;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public final class SettingsParser {
    private static final String DEFAULT_LANGUAGE = "ru";
    private static final int DEFAULT_CAPTURE_SECONDS = 20;
    private static final double DEFAULT_REWARD_AMOUNT = 25.0D;

    public PluginSettings parse(ConfigurationSection config, Consumer<String> warningSink) {
        String language = normalizeLanguage(config.getString("settings.language", DEFAULT_LANGUAGE), warningSink);
        int captureSeconds = positiveInt(
                config.getInt("settings.capture-time", DEFAULT_CAPTURE_SECONDS),
                DEFAULT_CAPTURE_SECONDS,
                "settings.capture-time",
                warningSink
        );
        double rewardAmount = positiveDouble(
                config.getDouble("settings.reward-amount", DEFAULT_REWARD_AMOUNT),
                DEFAULT_REWARD_AMOUNT,
                "settings.reward-amount",
                warningSink
        );
        boolean respectCancelledPhysicalEvents = config.getBoolean("settings.respect-cancelled-physical-events", false);

        return new PluginSettings(
                language,
                captureSeconds,
                rewardAmount,
                respectCancelledPhysicalEvents,
                loadSchedule(config, warningSink),
                loadPlatePosition(config)
        );
    }

    private String normalizeLanguage(String value, Consumer<String> warningSink) {
        if (value == null || value.isBlank()) {
            return DEFAULT_LANGUAGE;
        }

        String language = value.toLowerCase(Locale.ROOT);
        if (language.equals("ru") || language.equals("en")) {
            return language;
        }

        warningSink.accept("Unsupported settings.language '" + value + "'. Falling back to ru.");
        return DEFAULT_LANGUAGE;
    }

    private int positiveInt(int value, int fallback, String path, Consumer<String> warningSink) {
        if (value > 0) {
            return value;
        }
        warningSink.accept("Invalid " + path + " '" + value + "'. Falling back to " + fallback + ".");
        return fallback;
    }

    private double positiveDouble(double value, double fallback, String path, Consumer<String> warningSink) {
        if (value > 0.0D) {
            return value;
        }
        warningSink.accept("Invalid " + path + " '" + value + "'. Falling back to " + fallback + ".");
        return fallback;
    }

    private ScheduleSettings loadSchedule(ConfigurationSection config, Consumer<String> warningSink) {
        boolean enabled = config.getBoolean("settings.schedule.enabled", false);
        ZoneId zone = loadZone(config.getString("settings.schedule.timezone", ZoneId.systemDefault().getId()), warningSink);
        List<TimeWindow> windows = new ArrayList<>();

        for (Map<?, ?> rawWindow : config.getMapList("settings.schedule.windows")) {
            Object start = rawWindow.get("start");
            Object end = rawWindow.get("end");
            if (start == null || end == null) {
                warningSink.accept("Skipping schedule window without start/end.");
                continue;
            }

            try {
                windows.add(new TimeWindow(LocalTime.parse(start.toString()), LocalTime.parse(end.toString())));
            } catch (DateTimeException exception) {
                warningSink.accept("Skipping invalid schedule window: " + start + " - " + end);
            }
        }

        if (enabled && windows.isEmpty()) {
            warningSink.accept("Schedule is enabled but has no valid windows. The point will stay inactive.");
        }

        return new ScheduleSettings(enabled, zone, List.copyOf(windows));
    }

    private ZoneId loadZone(String timezone, Consumer<String> warningSink) {
        try {
            return ZoneId.of(timezone);
        } catch (DateTimeException exception) {
            warningSink.accept("Invalid settings.schedule.timezone '" + timezone + "'. Falling back to server timezone.");
            return ZoneId.systemDefault();
        }
    }

    private BlockPosition loadPlatePosition(ConfigurationSection config) {
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
