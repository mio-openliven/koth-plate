package me.openliven.kothplate.capture;

import org.bukkit.GameMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CaptureEligibilityTest {
    @Test
    void survivalAndAdventureCanCapture() {
        assertTrue(CaptureEligibility.canCapture(GameMode.SURVIVAL, false));
        assertTrue(CaptureEligibility.canCapture(GameMode.ADVENTURE, false));
    }

    @Test
    void creativeSpectatorAndDeadPlayersCannotCapture() {
        assertFalse(CaptureEligibility.canCapture(GameMode.CREATIVE, false));
        assertFalse(CaptureEligibility.canCapture(GameMode.SPECTATOR, false));
        assertFalse(CaptureEligibility.canCapture(GameMode.SURVIVAL, true));
    }
}
