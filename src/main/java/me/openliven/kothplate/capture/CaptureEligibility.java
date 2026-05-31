package me.openliven.kothplate.capture;

import org.bukkit.GameMode;

public final class CaptureEligibility {
    private CaptureEligibility() {
    }

    public static boolean canCapture(GameMode gameMode, boolean dead) {
        return !dead && gameMode != GameMode.CREATIVE && gameMode != GameMode.SPECTATOR;
    }
}
