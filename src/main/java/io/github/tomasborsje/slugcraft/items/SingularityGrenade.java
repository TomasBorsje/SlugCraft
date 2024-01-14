package io.github.tomasborsje.slugcraft.items;

import io.github.tomasborsje.slugcraft.core.Registration;
import io.github.tomasborsje.slugcraft.entities.ThrownSingularityGrenade;
import io.github.tomasborsje.slugcraft.entities.ThrownSporePuff;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SingularityGrenade extends Item {
    public SingularityGrenade() {
        super(Registration.ITEM_PROPERTIES);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            ThrownSingularityGrenade thrownSingularityGrenade = new ThrownSingularityGrenade(level, player);
            thrownSingularityGrenade.setItem(stack);
            thrownSingularityGrenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(thrownSingularityGrenade);
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("slugcraft.singularity_grenade_description"));
        pTooltipComponents.add(Component.literal(""));
        pTooltipComponents.add(Component.translatable("slugcraft.singularity_grenade_description_two"));
    }
}
