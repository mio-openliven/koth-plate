package me.openliven.kothplate.schedule;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleServiceTest {
    private static final ZoneId UTC = ZoneId.of("UTC");

    @Test
    void disabledScheduleIsAlwaysActive() {
        ScheduleSettings settings = new ScheduleSettings(false, UTC, List.of());
        ScheduleService service = new ScheduleService(settings, fixedClock("2026-06-01T12:00:00Z"));

        assertTrue(service.isActiveNow());
    }

    @Test
    void enabledScheduleWithoutWindowsIsInactive() {
        ScheduleSettings settings = new ScheduleSettings(true, UTC, List.of());
        ScheduleService service = new ScheduleService(settings, fixedClock("2026-06-01T12:00:00Z"));

        assertFalse(service.isActiveNow());
    }

    @Test
    void enabledScheduleUsesConfiguredWindows() {
        ScheduleSettings settings = new ScheduleSettings(
                true,
                UTC,
                List.of(new TimeWindow(LocalTime.parse("10:00"), LocalTime.parse("13:00")))
        );

        assertTrue(new ScheduleService(settings, fixedClock("2026-06-01T12:00:00Z")).isActiveNow());
        assertFalse(new ScheduleService(settings, fixedClock("2026-06-01T14:00:00Z")).isActiveNow());
    }

    private Clock fixedClock(String instant) {
        return Clock.fixed(Instant.parse(instant), UTC);
    }
}
