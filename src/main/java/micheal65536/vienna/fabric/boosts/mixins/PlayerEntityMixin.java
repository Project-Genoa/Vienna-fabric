package micheal65536.vienna.fabric.boosts.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import micheal65536.vienna.fabric.boosts.BoostStatusEffects;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
	@ModifyReturnValue(method = "getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F", at = @At(value = "RETURN"))
	private float getBlockBreakingSpeed(float speed)
	{
		if (((PlayerEntity) (Object) this).hasStatusEffect(BoostStatusEffects.MINING_SPEED))
		{
			int level = ((PlayerEntity) (Object) this).getStatusEffect(BoostStatusEffects.MINING_SPEED).getAmplifier() + 1;
			speed *= 1.0f + level * 0.25f;
		}
		return speed;
	}
}