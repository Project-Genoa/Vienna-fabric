package micheal65536.vienna.fabric.health.mixins;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(HungerManager.class)
public class HungerManagerMixin
{
	@Overwrite
	public void update(PlayerEntity player)
	{
		// empty
	}
}