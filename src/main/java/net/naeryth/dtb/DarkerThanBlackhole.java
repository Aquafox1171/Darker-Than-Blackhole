package net.naeryth.dtb;

import net.fabricmc.api.ModInitializer;


import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkerThanBlackhole implements ModInitializer {
	public static final String MOD_ID = "dtb";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		DTBConfigManager.load();
		LOGGER.info("[DTB] Initialized");
	}

}