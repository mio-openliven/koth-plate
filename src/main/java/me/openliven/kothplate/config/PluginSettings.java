package me.openliven.kothplate.config;

import me.openliven.kothplate.model.BlockPosition;
import me.openliven.kothplate.schedule.ScheduleSettings;

public record PluginSettings(
        String language,
        int captureSeconds,
        int visualHoldBufferSeconds,
        double rewardAmount,
        boolean respectCancelledPhysicalEvents,
        VisualSettings visuals,
        ScheduleSettings schedule,
        BlockPosition platePosition
) {
}
