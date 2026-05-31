package me.openliven.kothplate.schedule;

import java.time.LocalTime;

public record TimeWindow(LocalTime start, LocalTime end) {
    public boolean contains(LocalTime time) {
        if (start.equals(end)) {
            return true;
        }
        if (start.isBefore(end)) {
            return !time.isBefore(start) && time.isBefore(end);
        }
        return !time.isBefore(start) || time.isBefore(end);
    }
}
