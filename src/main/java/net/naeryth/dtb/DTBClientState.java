package net.naeryth.dtb;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

public final class DTBClientState {
		// Start with default; replaced on first sync packet
		public static DTBConfig active = DTBConfig.DEFAULT;
		// If true, we’re in a server-authoritative world
		public static boolean lockedByServer = false;


		public static void initialize() {

		}
	}