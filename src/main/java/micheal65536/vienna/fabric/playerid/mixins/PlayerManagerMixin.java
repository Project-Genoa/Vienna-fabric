package micheal65536.vienna.fabric.playerid.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import micheal65536.vienna.buildplate.connector.model.FindPlayerIdRequest;
import micheal65536.vienna.fabric.Main;
import micheal65536.vienna.fabric.ViennaPlayerJoinHandler;
import micheal65536.vienna.fabric.eventbus.EventBusMinecraftServer;
import micheal65536.vienna.fabric.playerid.EarthPlayer;
import micheal65536.vienna.fabric.playerid.SetEarthPlayer;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin
{
	@Inject(method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/server/network/ConnectedClientData;)V", at = @At(value = "TAIL"))
	private void onPlayerConnect(ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity, ConnectedClientData connectedClientData, CallbackInfo callbackInfo)
	{
		FindPlayerIdRequest findPlayerIdRequest = new FindPlayerIdRequest(serverPlayerEntity.getGameProfile().getId().toString(), serverPlayerEntity.getGameProfile().getName());
		String earthPlayerId = ((EventBusMinecraftServer) serverPlayerEntity.server).getEventBusHelper().doRequestResponseSync("findPlayer", findPlayerIdRequest, String.class);
		((SetEarthPlayer) serverPlayerEntity).setEarthPlayerId(earthPlayerId);

		if (earthPlayerId != null)
		{
			Main.LOGGER.info("Player {} {} is Vienna player {}", serverPlayerEntity.getName().getString(), serverPlayerEntity.getGameProfile().getId().toString(), earthPlayerId);
			ViennaPlayerJoinHandler.handleJoin((EarthPlayer) serverPlayerEntity);
		}
		else
		{
			Main.LOGGER.warn("Vienna player ID not found for {} {}", serverPlayerEntity.getName().getString(), serverPlayerEntity.getGameProfile().getId().toString());
		}
	}
}