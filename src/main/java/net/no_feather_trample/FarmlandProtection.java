package net.no_feather_trample;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class FarmlandProtection {
    private FarmlandProtection() {}

    public static boolean protects(Entity cause) {
        if (!(cause instanceof LivingEntity living)) return false;
        if (living.hasEffect(MobEffects.SLOW_FALLING)) return true;
        var featherFalling = living.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FEATHER_FALLING);
        return EnchantmentHelper.getEnchantmentLevel(featherFalling, living) > 0;
    }
}
