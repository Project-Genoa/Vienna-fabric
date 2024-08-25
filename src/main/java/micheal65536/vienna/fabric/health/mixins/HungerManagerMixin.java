package micheal65536.vienna.fabric.health.mixins;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HungerManager.class)
public class HungerManagerMixin
{
	@Overwrite
	public void update(PlayerEntity player)
	{
		// TODO: these probably aren't necessary, I just have them here because it allows regular unmodified client to eat
		((HungerManagerAccessor) this).setFoodLevel(10);
		((HungerManagerAccessor) this).setSaturationLevel(0.0f);
		((HungerManagerAccessor) this).setExhaustion(0.0f);
	}

	@Mixin(HungerManager.class)
	public interface HungerManagerAccessor
	{
		@Accessor
		void setFoodLevel(int foodLevel);

		@Accessor
		void setSaturationLevel(float saturationLevel);

		@Accessor
		void setExhaustion(float exhaustion);
	}
}