package micheal65536.vienna.fabric.startupshutdown.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import micheal65536.vienna.buildplate.connector.model.WorldSavedMessage;
import micheal65536.vienna.fabric.Main;
import micheal65536.vienna.fabric.eventbus.EventBusHelper;
import micheal65536.vienna.fabric.eventbus.EventBusMinecraftServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin
{
	@Inject(method = "save(ZZZ)Z", at = @At(value = "TAIL"))
	private void save(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> callbackInfo)
	{
		// this makes sure that all the queued chunks are actually written to file, I think
		for (ServerWorld serverWorld : ((MinecraftServer) (Object) this).getWorlds())
		{
			serverWorld.getChunkManager().threadedAnvilChunkStorage.completeAll();
			((ServerWorldAccessor) serverWorld).getEntityManager().flush();
		}

		byte[] data;
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream))
		{
			File worldDir = ((MinecraftServer) (Object) this).getSavePath(WorldSavePath.ROOT).toFile();
			for (String dirName : new String[]{"region", "entities"})
			{
				File dir = new File(worldDir, dirName);
				for (String regionName : new String[]{"r.0.0.mca", "r.0.-1.mca", "r.-1.0.mca", "r.-1.-1.mca"})
				{
					ZipEntry zipEntry = new ZipEntry(dirName + "/" + regionName);
					zipEntry.setMethod(ZipEntry.DEFLATED);
					zipOutputStream.putNextEntry(zipEntry);
					try (FileInputStream fileInputStream = new FileInputStream(new File(dir, regionName)))
					{
						fileInputStream.transferTo(zipOutputStream);
					}
					zipOutputStream.closeEntry();
				}
			}

			zipOutputStream.finish();
			data = byteArrayOutputStream.toByteArray();
		}
		catch (IOException exception)
		{
			Main.LOGGER.error("Could not get saved world data", exception);
			return;
		}

		// TODO: include parameter indicating if this is the final world save before the server shuts down
		Main.LOGGER.info("Sending saved world data");
		EventBusHelper eventBusHelper = ((EventBusMinecraftServer) this).getEventBusHelper();
		eventBusHelper.publishJson("saved", new WorldSavedMessage(Base64.getEncoder().encodeToString(data)));
	}

	@Inject(method = "shutdown()V", at = @At(value = "TAIL"))
	private void shutdown(CallbackInfo callbackInfo)
	{
		Main.LOGGER.info("Notifying server shutdown");
		EventBusHelper eventBusHelper = ((EventBusMinecraftServer) this).getEventBusHelper();
		eventBusHelper.publishJson("stopping", null);
	}

	@Mixin(ServerWorld.class)
	public interface ServerWorldAccessor
	{
		@Accessor("entityManager")
		ServerEntityManager<Entity> getEntityManager();
	}
}