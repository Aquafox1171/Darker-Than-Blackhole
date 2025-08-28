package net.naeryth.dtb;

public record DTBConfig(boolean enabled, float strength, float gamma, boolean respectNightVision, int version) {
    public static DTBConfig DEFAULT = new DTBConfig(true, 0.65f, 2.4f, true, 1);
}