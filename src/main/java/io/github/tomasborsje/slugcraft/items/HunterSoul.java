package io.github.tomasborsje.slugcraft.items;

import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HunterSoul extends Item {
    public HunterSoul() {
        super(Registration.ITEM_PROPERTIES);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if(pIsSelected && pEntity instanceof Player player) {
            // Hunter soul grants 4 extra hearts of damage per hit
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 3, false, false));
            if(!player.hasEffect(Registration.ROTTING.get())) {
                player.addEffect(new MobEffectInstance(Registration.ROTTING.get(), -1, 0, false, false));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("slugcraft.hunter_power"));
        pTooltipComponents.add(Component.translatable("slugcraft.hunter_power_two"));
        pTooltipComponents.add(Component.literal(""));
        pTooltipComponents.add(Component.translatable("slugcraft.hunter_haiku_one"));
        pTooltipComponents.add(Component.translatable("slugcraft.hunter_haiku_two"));
        pTooltipComponents.add(Component.translatable("slugcraft.hunter_haiku_three"));
    }
}
