package me.openliven.kothplate.capture;

import java.util.UUID;

public final class CaptureProgress {
    private UUID playerId;
    private int remainingSeconds;

    public CaptureTick tick(UUID nextPlayerId, int captureSeconds) {
        if (!nextPlayerId.equals(playerId)) {
            playerId = nextPlayerId;
            remainingSeconds = captureSeconds;
        }

        int displayedSeconds = remainingSeconds;
        remainingSeconds--;
        return new CaptureTick(displayedSeconds, remainingSeconds <= 0);
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
