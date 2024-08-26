package micheal65536.vienna.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import micheal65536.vienna.fabric.boosts.BoostStatusEffects;

public class Main implements ModInitializer, DedicatedServerModInitializer, ClientModInitializer
{
	public static final Logger LOGGER = LoggerFactory.getLogger("vienna");

	@Override
	public void onInitialize()
	{
		BoostStatusEffects.init();
	}

	@Override
	public void onInitializeServer()
	{
		// empty
	}

	@Override
	public void onInitializeClient()
	{
		// empty
	}
}