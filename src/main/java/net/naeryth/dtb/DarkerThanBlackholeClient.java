package net.naeryth.dtb;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;


public class DarkerThanBlackholeClient implements ClientModInitializer {
	private static final float BRIGHTNESS_THRESHOLD = 0.10f; // 0..1, tweak to taste
	private final DTBPostLoader post = new DTBPostLoader();
	private boolean lastEnabled = false;

	@Override
	public void onInitializeClient() {
		// Optional: your config init
		DTBClientState.initialize();

		// Invalidate probe cache on resource reloads (datapack/shader reload)
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
				.registerReloadListener(new SimpleSynchronousResourceReloadListener() {
					@Override public Identifier getFabricId() {
						return Identifier.of(DarkerThanBlackhole.MOD_ID, "reload_listener");
					}
					@Override public void reload(ResourceManager manager) {
						post.invalidate();
						lastEnabled = false;
						// ensure off after reload; will re-enable next tick if needed
						MinecraftClient c = MinecraftClient.getInstance();
						if (c != null && c.gameRenderer != null) post.disable(c);
						System.out.println("[DTB] Resources reloaded; post id cache cleared");
					}
				});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.world == null || client.player == null) return;

			// Decide if we want the post on
			float brightness = client.player.getBrightnessAtEyes(); // 0..1
			boolean darkHere = brightness < BRIGHTNESS_THRESHOLD;

			// optional: also gate by night time
			long t = client.world.getTimeOfDay() % 24000L;
			boolean isNight = (t >= 13000 && t <= 23000);

			DTBConfig cfg = DTBClientState.active;
			boolean nv = cfg.respectNightVision() &&
					client.player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.NIGHT_VISION);

			boolean shouldEnable = cfg.enabled() && darkHere && isNight && !nv;

			if (shouldEnable != lastEnabled) {
				if (shouldEnable) post.enable(client);
				else post.disable(client);
				lastEnabled = shouldEnable;
			}
		});
	}
}
