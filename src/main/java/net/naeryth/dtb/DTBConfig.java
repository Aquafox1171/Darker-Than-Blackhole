package net.naeryth.dtb;

public record DTBConfig(
        boolean enabled,
        float strength,
        float gamma,
        boolean respectNightVision,
        int version
) {
    public static final DTBConfig DEFAULT = new DTBConfig(true, 0.75f, 2.0f, true, 1);
}
