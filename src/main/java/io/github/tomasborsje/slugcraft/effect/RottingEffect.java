package io.github.tomasborsje.slugcraft.effect;

import io.github.tomasborsje.slugcraft.core.Registration;
import io.github.tomasborsje.slugcraft.quickfire.IQuickfireCapability;
import io.github.tomasborsje.slugcraft.quickfire.QuickfireCapability;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.util.LazyOptional;

import java.util.UUID;

public class RottingEffect extends MobEffect {
    public RottingEffect() {
        super(MobEffectCategory.HARMFUL, 255); // Blue colour
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) { }

}
