package me.openliven.kothplate.plate;

public record PlateDiagnostic(Status status, String blockMaterial) {
    public enum Status {
        NOT_CONFIGURED,
        WORLD_NOT_LOADED,
        CHUNK_NOT_LOADED,
        READY,
        WRONG_BLOCK
    }
}
