package me.openliven.kothplate.service;

public record EconomyDepositResult(boolean successful, String errorMessage) {
    public static EconomyDepositResult success() {
        return new EconomyDepositResult(true, "");
    }

    public static EconomyDepositResult failure(String errorMessage) {
        return new EconomyDepositResult(false, errorMessage == null ? "unknown error" : errorMessage);
    }
}
