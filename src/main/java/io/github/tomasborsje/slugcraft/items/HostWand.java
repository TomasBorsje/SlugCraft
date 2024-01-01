package io.github.tomasborsje.slugcraft.items;

import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HostWand extends Item {
    public HostWand() {
        super(Registration.ITEM_PROPERTIES);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(!pLevel.isClientSide && pUsedHand == InteractionHand.MAIN_HAND) {
            pPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

            // Get level capability
            pLevel.getCapability(Registration.QUICKFIRE_HANDLER).ifPresent(quickFire -> {
                quickFire.startRound(pLevel, pPlayer);
            });
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {


        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }



    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("slugcraft.host_wand_description"));
    }
}
