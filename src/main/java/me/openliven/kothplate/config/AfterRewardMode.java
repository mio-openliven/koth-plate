package me.openliven.kothplate.config;

public enum AfterRewardMode {
    OFF(0, 0.0D, false),
    MINI_AIR_BURST(1, 0.28D, true),
    EJECT_WINNER(2, 0.85D, true);

    private final int id;
    private final double horizontalStrength;
    private final boolean airBurst;

    AfterRewardMode(int id, double horizontalStrength, boolean airBurst) {
        this.id = id;
        this.horizontalStrength = horizontalStrength;
        this.airBurst = airBurst;
    }

    public static AfterRewardMode fromId(int id) {
        for (AfterRewardMode mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }
        return null;
    }

    public boolean movesWinner() {
        return horizontalStrength > 0.0D;
    }

    public double horizontalStrength() {
        return horizontalStrength;
    }

    public boolean airBurst() {
        return airBurst;
    }
}
