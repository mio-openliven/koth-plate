package me.openliven.kothplate.capture;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CaptureProgressTest {
    @Test
    void completesAfterConfiguredNumberOfTicksForSamePlayer() {
        CaptureProgress progress = new CaptureProgress();
        UUID playerId = UUID.randomUUID();

        CaptureProgress.CaptureTick first = progress.tick(playerId, 3);
        CaptureProgress.CaptureTick second = progress.tick(playerId, 3);
        CaptureProgress.CaptureTick third = progress.tick(playerId, 3);

        assertEquals(3, first.displayedSeconds());
        assertFalse(first.completed());
        assertEquals(2, second.displayedSeconds());
        assertFalse(second.completed());
        assertEquals(1, third.displayedSeconds());
        assertTrue(third.completed());
    }

    @Test
    void switchesTimerWhenAnotherPlayerStepsOnPlate() {
        CaptureProgress progress = new CaptureProgress();
        UUID firstPlayer = UUID.randomUUID();
        UUID secondPlayer = UUID.randomUUID();

        progress.tick(firstPlayer, 20);
        CaptureProgress.CaptureTick switched = progress.tick(secondPlayer, 20);

        assertEquals(20, switched.displayedSeconds());
        assertEquals(secondPlayer, progress.playerId());
    }
}
