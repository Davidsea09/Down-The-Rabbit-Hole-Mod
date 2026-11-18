package net.emanueljdf09.dtrhmod.util.components;

public class ClientProgressionCache {
    private static boolean nearLockedBiome = false;

    public static boolean isNearLockedBiome() {
        return nearLockedBiome;
    }

    public static void setNearLockedBiome(boolean locked) {
        nearLockedBiome = locked;
    }
}