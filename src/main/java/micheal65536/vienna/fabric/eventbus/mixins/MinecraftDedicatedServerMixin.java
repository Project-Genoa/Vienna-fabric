package micheal65536.vienna.fabric.eventbus.mixins;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import micheal65536.vienna.fabric.Main;
import micheal65536.vienna.fabric.eventbus.EventBusHelper;
import micheal65536.vienna.fabric.eventbus.EventBusMinecraftServer;
import micheal65536.vienna.fabric.eventbus.EventBusServerPropertiesHandler;

@Mixin(MinecraftDedicatedServer.class)
public class MinecraftDedicatedServerMixin implements EventBusMinecraftServer
{
	private EventBusHelper eventBusHelper = null;

	@Inject(method = "setupServer()Z", at = @At(value = "HEAD"), cancellable = true)
	private void setupServer(CallbackInfoReturnable<Boolean> callbackInfo)
	{
		EventBusServerPropertiesHandler eventBusServerPropertiesHandler = (EventBusServerPropertiesHandler) ((MinecraftDedicatedServer) (Object) this).getProperties();
		String eventBusAddress = eventBusServerPropertiesHandler.getEventBusAddress();
		String eventBusQueueName = eventBusServerPropertiesHandler.getEventBusQueueName();
		if (eventBusAddress.isEmpty() || eventBusQueueName.isEmpty())
		{
			throw new RuntimeException("Event bus address or queue name was not provided");
		}
		Main.LOGGER.info("Attempting to connect to event bus");
		this.eventBusHelper = new EventBusHelper(eventBusAddress, eventBusQueueName);
		Main.LOGGER.info("Connected to event bus");
	}

	@Inject(method = "shutdown()V", at = @At(value = "TAIL"))
	private void shutdown(CallbackInfo callbackInfo)
	{
		if (this.eventBusHelper != null)
		{
			Main.LOGGER.info("Disconnecting from event bus");
			this.eventBusHelper.close();
			Main.LOGGER.info("Disconnected from event bus");
			this.eventBusHelper = null;
		}
	}

	@Override
	@NotNull
	public EventBusHelper getEventBusHelper()
	{
		if (this.eventBusHelper == null)
		{
			throw new IllegalStateException("Attempt to get event bus helper when it is null, this should only happen during server startup/shutdown or if an error occurs");
		}
		return this.eventBusHelper;
	}
}