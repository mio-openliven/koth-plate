package me.openliven.kothplate.config;

import me.openliven.kothplate.model.BlockPosition;
import me.openliven.kothplate.schedule.ScheduleSettings;

public record PluginSettings(
        String language,
        int captureSeconds,
        double rewardAmount,
        boolean respectCancelledPhysicalEvents,
        ScheduleSettings schedule,
        BlockPosition platePosition
) {
}
