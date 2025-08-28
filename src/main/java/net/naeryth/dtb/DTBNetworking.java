package net.naeryth.dtb;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class DTBNetworking {
    private DTBNetworking() {}

    /** hook server events once from your ModInitializer#onInitialize() */
    public static void initServerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            // send current server config to the joining player
            sendConfig(handler.player, DTBConfigManager.CURRENT);
        });
    }

    /** send to one player */
    public static void sendConfig(ServerPlayerEntity p, DTBConfig cfg) {
        ServerPlayNetworking.send(p, new DTBConfigSyncPayload(
            cfg.version(), cfg.enabled(), cfg.strength(), cfg.gamma(), cfg.respectNightVision()
        ));
    }

    /** broadcast to everyone (use after /tdreload) */
    public static void broadcastConfig(MinecraftServer server) {
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            sendConfig(p, DTBConfigManager.CURRENT);
        }
    }
}