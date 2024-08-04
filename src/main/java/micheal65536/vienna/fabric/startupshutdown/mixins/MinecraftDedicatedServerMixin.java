package micheal65536.vienna.fabric.startupshutdown.mixins;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import micheal65536.vienna.fabric.Main;
import micheal65536.vienna.fabric.eventbus.EventBusHelper;
import micheal65536.vienna.fabric.eventbus.EventBusMinecraftServer;

@Mixin(MinecraftDedicatedServer.class)
public class MinecraftDedicatedServerMixin
{
	@Inject(method = "setupServer()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;loadWorld()V"))
	private void setupServer(CallbackInfoReturnable<Boolean> callbackInfo)
	{
		Main.LOGGER.info("Notifying server ready");
		EventBusHelper eventBusHelper = ((EventBusMinecraftServer) this).getEventBusHelper();
		eventBusHelper.publishJson("started", null);
	}
}