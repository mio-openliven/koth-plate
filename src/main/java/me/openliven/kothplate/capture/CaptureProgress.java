package me.openliven.kothplate.capture;

import java.util.UUID;

public final class CaptureProgress {
    private UUID playerId;
    private int remainingSeconds;

    public CaptureTick tick(UUID nextPlayerId, int captureSeconds, int visualBufferSeconds) {
        if (!nextPlayerId.equals(playerId)) {
            playerId = nextPlayerId;
            remainingSeconds = captureSeconds + visualBufferSeconds;
        }

        int displayedSeconds = remainingSeconds;
        boolean completed = remainingSeconds <= 1;
        remainingSeconds--;
        return new CaptureTick(displayedSeconds, completed);
    }

    public UUID playerId() {
        return playerId;
    }

    public void reset(int captureSeconds) {
        playerId = null;
        remainingSeconds = captureSeconds;
    }

    public record CaptureTick(int displayedSeconds, boolean completed) {
    }
}
