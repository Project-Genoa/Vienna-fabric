package micheal65536.vienna.fabric.playerid.mixins;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import micheal65536.vienna.fabric.playerid.EarthPlayer;
import micheal65536.vienna.fabric.playerid.SetEarthPlayer;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements EarthPlayer, SetEarthPlayer
{
	private String earthPlayerId = null;

	@Override
	@Nullable
	public String getEarthPlayerId()
	{
		return this.earthPlayerId;
	}

	@Override
	public void setEarthPlayerId(@Nullable String playerId)
	{
		this.earthPlayerId = playerId;
	}
}