package draylar.tiered.util;

import draylar.tiered.api.CustomEntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;

public class AttributeHelper {

    public static boolean shouldMeeleCrit(PlayerEntity playerEntity) {
        EntityAttributeInstance instance = playerEntity.getAttributeInstance(CustomEntityAttributes.CRIT_CHANCE);
        if (instance != null) {
            float critChance = 0.0f;
            for (EntityAttributeModifier modifier : instance.getModifiers()) {
                float amount = (float) modifier.getValue();
                critChance += amount;
            }
            return playerEntity.getRandom().nextDouble() < critChance;
        }
        return false;
    }

    public static float getExtraDigSpeed(PlayerEntity playerEntity, float oldDigSpeed) {
        EntityAttributeInstance instance = playerEntity.getAttributeInstance(CustomEntityAttributes.DIG_SPEED);
        if (instance != null) {
            float extraDigSpeed = oldDigSpeed;
            for (EntityAttributeModifier modifier : instance.getModifiers()) {
                float amount = (float) modifier.getValue();

                if (modifier.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                    extraDigSpeed += amount;
                } else {
                    extraDigSpeed *= (amount + 1);
                }
            }
            return extraDigSpeed;
        }

        return oldDigSpeed;
    }

    public static float getExtraRangeDamage(PlayerEntity playerEntity, float oldDamage) {
        EntityAttributeInstance instance = playerEntity.getAttributeInstance(CustomEntityAttributes.RANGE_ATTACK_DAMAGE);
        if (instance != null) {
            float rangeDamage = oldDamage;
            for (EntityAttributeModifier modifier : instance.getModifiers()) {
                float amount = (float) modifier.getValue();

                if (modifier.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                    rangeDamage += amount;
                } else {
                    rangeDamage *= (amount + 1.0f);
                }
            }
            return Math.min(rangeDamage, Integer.MAX_VALUE);
        }
        return oldDamage;
    }

    public static float getExtraCritDamage(PlayerEntity playerEntity, float oldDamage) {
        EntityAttributeInstance instance = playerEntity.getAttributeInstance(CustomEntityAttributes.CRIT_CHANCE);
        float customChance = 0.0f;
        if (instance != null) {
            for (EntityAttributeModifier modifier : instance.getModifiers()) {
                customChance += (float) modifier.getValue();
            }
        }
        if (playerEntity.world.getRandom().nextFloat() > (1.0f - Math.abs(customChance))) {
            float extraCrit = oldDamage;
            if (customChance < 0.0f) {
                extraCrit = extraCrit / 2.0f;
            }
            return Math.min(customChance > 0.0f ? extraCrit : -extraCrit, Integer.MAX_VALUE);
        }

        return oldDamage;
    }

}
