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

public class RivuletSoul extends Item {
    private static final Properties PROPERTIES = new Properties();
    public RivuletSoul() {
        super(Registration.ITEM_PROPERTIES);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        // Check the item is in the player's offhand
        if (pEntity instanceof Player player && player.getOffhandItem() == pStack) {
            // Rivulet soul grants speed and jump height
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 20, 5, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 3, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 0, false, false));
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("slugcraft.rivulet_power"));
        pTooltipComponents.add(Component.translatable("slugcraft.rivulet_power_two"));
        pTooltipComponents.add(Component.literal(""));
        pTooltipComponents.add(Component.translatable("slugcraft.rivulet_haiku_one"));
        pTooltipComponents.add(Component.translatable("slugcraft.rivulet_haiku_two"));
        pTooltipComponents.add(Component.translatable("slugcraft.rivulet_haiku_three"));
    }
}
