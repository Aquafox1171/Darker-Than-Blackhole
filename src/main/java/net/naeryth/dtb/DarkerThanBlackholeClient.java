package net.naeryth.dtb;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;


public class DarkerThanBlackholeClient implements ClientModInitializer {


	@Override
	public void onInitializeClient() {
		DTBClientState.initialize();
		ClientPlayNetworking.registerGlobalReceiver(
				DTBConfigSyncPayload.ID,
				(DTBConfigSyncPayload payload, ClientPlayNetworking.Context ctx) -> {
					var cfg = new DTBConfig(
							payload.enabled(), payload.strength(), payload.gamma(), payload.respectNV(), payload.version()
					);
					ctx.client().execute(() -> {
						DTBClientState.active = cfg;
						DTBClientState.lockedByServer = true;
					});
				}
		);

	}

}