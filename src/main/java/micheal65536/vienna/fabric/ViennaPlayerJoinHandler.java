package micheal65536.vienna.fabric;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import micheal65536.vienna.buildplate.connector.model.InitialPlayerStateResponse;
import micheal65536.vienna.fabric.boosts.BoostStatusEffects;
import micheal65536.vienna.fabric.eventbus.EventBusMinecraftServer;
import micheal65536.vienna.fabric.playerid.EarthPlayer;

public final class ViennaPlayerJoinHandler
{
	public static void handleJoin(@NotNull ServerPlayerEntity serverPlayerEntity)
	{
		serverPlayerEntity.getInventory().clear();
		serverPlayerEntity.clearStatusEffects();

		String playerId = ((EarthPlayer) serverPlayerEntity).getEarthPlayerId();
		InitialPlayerStateResponse initialPlayerStateResponse = ((EventBusMinecraftServer) serverPlayerEntity.server).getEventBusHelper().doRequestResponseSync("getInitialPlayerState", playerId, InitialPlayerStateResponse.class);
		if (initialPlayerStateResponse != null)
		{
			for (InitialPlayerStateResponse.BoostStatusEffect boostStatusEffect : initialPlayerStateResponse.boostStatusEffects())
			{
				serverPlayerEntity.addStatusEffect(new StatusEffectInstance(
						switch (boostStatusEffect.type())
						{
							case ADVENTURE_XP -> BoostStatusEffects.ADVENTURE_XP;
							case DEFENSE -> BoostStatusEffects.DEFENSE;
							case EATING -> BoostStatusEffects.EATING;
							case HEALTH -> BoostStatusEffects.HEALTH;
							case MINING_SPEED -> BoostStatusEffects.MINING_SPEED;
							case STRENGTH -> BoostStatusEffects.STRENGTH;
						},
						(int) ((boostStatusEffect.remainingDuration() * 20) / 1000),
						switch (boostStatusEffect.type())
						{
							case HEALTH -> boostStatusEffect.value() / 10 - 1;
							default -> boostStatusEffect.value() / 25 - 1;
						},
						false,
						false
				));
			}
			serverPlayerEntity.setHealth(initialPlayerStateResponse.health());
		}
		else
		{
			Main.LOGGER.warn("Did not get initial player state for player {}", playerId);
			serverPlayerEntity.setHealth(serverPlayerEntity.getMaxHealth());
		}
	}
}