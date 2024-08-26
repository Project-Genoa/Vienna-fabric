package micheal65536.vienna.fabric.health.mixins;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import micheal65536.vienna.fabric.boosts.BoostStatusEffects;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
	@Overwrite
	public boolean canConsume(boolean force)
	{
		if (force)
		{
			return true;
		}
		else
		{
			return ((PlayerEntity) (Object) this).getHealth() < ((PlayerEntity) (Object) this).getMaxHealth();
		}
	}

	@Redirect(method = "eatFood(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;eat(Lnet/minecraft/item/Item;Lnet/minecraft/item/ItemStack;)V"))
	public void eat(HungerManager hungerManager, Item item, ItemStack itemStack)
	{
		if (item.isFood())
		{
			float health = item.getFoodComponent().getHunger();
			if (((PlayerEntity) (Object) this).hasStatusEffect(BoostStatusEffects.EATING))
			{
				int level = ((PlayerEntity) (Object) this).getStatusEffect(BoostStatusEffects.EATING).getAmplifier() + 1;
				health *= 1.0f + (level * 0.25f);
			}
			((PlayerEntity) (Object) this).heal(health);
		}
	}
}