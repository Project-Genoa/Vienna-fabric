package micheal65536.vienna.fabric.boosts.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import micheal65536.vienna.fabric.boosts.BoostStatusEffects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
	@ModifyReturnValue(method = "modifyAppliedDamage(Lnet/minecraft/entity/damage/DamageSource;F)F", at = @At(value = "RETURN"))
	private float modifyAppliedDamage(float damage, DamageSource damageSource)
	{
		if (damage <= 0.0f)
		{
			return damage;
		}
		if (damageSource.isIn(DamageTypeTags.BYPASSES_EFFECTS))
		{
			return damage;
		}

		if (((LivingEntity) (Object) this).hasStatusEffect(BoostStatusEffects.DEFENSE))
		{
			int level = ((LivingEntity) (Object) this).getStatusEffect(BoostStatusEffects.DEFENSE).getAmplifier() + 1;
			damage *= 1.0f - (level * 0.25f);
			return damage;
		}
		else
		{
			return damage;
		}
	}
}