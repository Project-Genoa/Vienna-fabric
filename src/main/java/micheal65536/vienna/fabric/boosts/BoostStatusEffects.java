package micheal65536.vienna.fabric.boosts;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class BoostStatusEffects
{
	// I am aware that some of these are near-duplicates of effects that already exist in vanilla Minecraft, however this is an easier way for us to apply our own modifier strengths (which don't match the strengths available in vanilla and would otherwise require overriding vanilla anyway)
	public static final StatusEffect ADVENTURE_XP = register("adventure_xp", new BoostStatusEffect());    // TODO
	public static final StatusEffect DEFENSE = register("defense", new BoostStatusEffect());
	public static final StatusEffect EATING = register("eating", new BoostStatusEffect());
	public static final StatusEffect HEALTH = register("health", new BoostStatusEffect().addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, "605079D4-5315-4FFE-8C3F-5174D4A47CC3", 0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
	public static final StatusEffect MINING_SPEED = register("mining_speed", new BoostStatusEffect());
	public static final StatusEffect STRENGTH = register("strength", new BoostStatusEffect().addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "EE3B2C55-D31A-4C03-82A5-346B69F2096B", 0.25, EntityAttributeModifier.Operation.MULTIPLY_BASE));

	@NotNull
	private static StatusEffect register(@NotNull String id, @NotNull StatusEffect statusEffect)
	{
		return Registry.register(Registries.STATUS_EFFECT, "vienna:" + id, statusEffect);
	}

	public static void init()
	{
		// empty
	}
}