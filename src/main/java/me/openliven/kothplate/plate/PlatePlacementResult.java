package me.openliven.kothplate.plate;

import me.openliven.kothplate.model.BlockPosition;

public record PlatePlacementResult(Status status, BlockPosition position) {
    public enum Status {
        SUCCESS,
        BASE_NEEDED,
        BLOCKED,
        NOT_PRESSURE_PLATE
    }
}
