package magichand.modid;

import magichand.modid.enchantments.EnchantmentRegistrator;
import magichand.modid.entity.EntityRegistrator;
import magichand.modid.items.ItemRegistrator;
import magichand.modid.networking.PacketRegistrator;
import magichand.modid.statuseffect.PotionRegistrator;
import magichand.modid.statuseffect.StatusEffectRegistrator;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagicHand implements ModInitializer {
	public static final String MOD_ID = "magic-hand";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		ItemRegistrator.initializeAndRegister();


		EntityRegistrator.initializeAndRegister();

		StatusEffectRegistrator.initializeAndRegister();
		PotionRegistrator.initializeAndRegister();

		EnchantmentRegistrator.initializeAndRegister();

		PacketRegistrator.initializeAndRegister();

	}
}