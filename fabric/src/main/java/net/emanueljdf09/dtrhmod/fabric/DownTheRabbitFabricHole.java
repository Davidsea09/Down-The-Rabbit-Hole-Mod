package net.emanueljdf09.dtrhmod.fabric;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownTheRabbitFabricHole implements ModInitializer {
	public static final String MOD_ID = "dtrhmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		DownTheRabbitHole.init();
	}
}
