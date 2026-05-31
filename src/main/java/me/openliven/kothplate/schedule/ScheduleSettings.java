package me.openliven.kothplate.schedule;

import java.time.ZoneId;
import java.util.List;

public record ScheduleSettings(boolean enabled, ZoneId zone, List<TimeWindow> windows) {
}
