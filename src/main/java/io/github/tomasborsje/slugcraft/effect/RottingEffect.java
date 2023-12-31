package io.github.tomasborsje.slugcraft.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class RottingEffect extends MobEffect {
    static int TICKS_PER_HURT = 60;
    public RottingEffect() {
        super(MobEffectCategory.HARMFUL, 255); // Blue colour
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        addAttributeModifier(Attributes.MAX_HEALTH, UUID.randomUUID().toString(), -2, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return pDuration % TICKS_PER_HURT == 0;
    }
}
