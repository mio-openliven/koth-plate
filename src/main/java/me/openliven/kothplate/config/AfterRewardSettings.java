package me.openliven.kothplate.config;

public record AfterRewardSettings(AfterRewardMode mode) {
    public boolean enabled() {
        return mode != AfterRewardMode.OFF;
    }
}
