package micheal65536.vienna.fabric.eventbus.mixins;

import net.minecraft.server.dedicated.AbstractPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import micheal65536.vienna.fabric.eventbus.EventBusServerPropertiesHandler;

import java.util.Properties;
import java.util.function.Function;

@Mixin(ServerPropertiesHandler.class)
public class ServerPropertiesHandlerMixin implements EventBusServerPropertiesHandler
{
	private String eventBusAddress;
	private String eventBusQueueName;

	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void init(Properties properties, CallbackInfo callbackInfo)
	{
		this.eventBusAddress = ((AbstractPropertiesHandlerAccessor) this).callGetString("vienna-event-bus-address", "");
		this.eventBusQueueName = ((AbstractPropertiesHandlerAccessor) this).callGetString("vienna-event-bus-queue-name", "");
	}

	@Mixin(AbstractPropertiesHandler.class)
	public interface AbstractPropertiesHandlerAccessor
	{
		@Invoker("get")
		<T> T callGet(String key, Function<String, T> parser, T defaultValue);

		@Invoker("getString")
		String callGetString(String key, String defaultValue);
	}

	@Override
	public String getEventBusAddress()
	{
		return this.eventBusAddress;
	}

	@Override
	public String getEventBusQueueName()
	{
		return this.eventBusQueueName;
	}
}