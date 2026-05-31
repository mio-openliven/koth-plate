package me.openliven.kothplate.schedule;

import java.time.Clock;
import java.time.LocalTime;

public final class ScheduleService {
    private final ScheduleSettings settings;
    private final Clock clock;

    public ScheduleService(ScheduleSettings settings, Clock clock) {
        this.settings = settings;
        this.clock = clock;
    }

    public boolean isActiveNow() {
        if (!settings.enabled()) {
            return true;
        }
        if (settings.windows().isEmpty()) {
            return false;
        }

        LocalTime now = LocalTime.now(clock.withZone(settings.zone()));
        for (TimeWindow window : settings.windows()) {
            if (window.contains(now)) {
                return true;
            }
        }
        return false;
    }
}
