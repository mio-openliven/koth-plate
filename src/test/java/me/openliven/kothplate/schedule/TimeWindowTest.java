package me.openliven.kothplate.schedule;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeWindowTest {
    @Test
    void containsTimeInsideSameDayWindow() {
        TimeWindow window = new TimeWindow(LocalTime.parse("18:00"), LocalTime.parse("23:00"));

        assertTrue(window.contains(LocalTime.parse("18:00")));
        assertTrue(window.contains(LocalTime.parse("20:30")));
        assertFalse(window.contains(LocalTime.parse("23:00")));
        assertFalse(window.contains(LocalTime.parse("12:00")));
    }

    @Test
    void containsTimeInsideOvernightWindow() {
        TimeWindow window = new TimeWindow(LocalTime.parse("22:00"), LocalTime.parse("02:00"));

        assertTrue(window.contains(LocalTime.parse("23:30")));
        assertTrue(window.contains(LocalTime.parse("01:30")));
        assertFalse(window.contains(LocalTime.parse("12:00")));
    }

    @Test
    void equalStartAndEndMeansAlwaysActive() {
        TimeWindow window = new TimeWindow(LocalTime.parse("00:00"), LocalTime.parse("00:00"));

        assertTrue(window.contains(LocalTime.parse("00:00")));
        assertTrue(window.contains(LocalTime.parse("15:45")));
    }
}
