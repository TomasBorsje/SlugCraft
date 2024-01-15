package io.github.tomasborsje.slugcraft.items;

import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GourmandSoul extends Item {
    public static final List<ResourceLocation> CRAFTABLES = List.of(
            new ResourceLocation("slugcraft", "explosive_spear"),
            new ResourceLocation("slugcraft", "spear"),
            new ResourceLocation("slugcraft", "spore_puff"),
            new ResourceLocation("minecraft", "golden_apple"),
            new ResourceLocation("minecraft", "lava_bucket")
    );
    public GourmandSoul() {
        super(Registration.ITEM_PROPERTIES);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("slugcraft.gourmand_power"));
        pTooltipComponents.add(Component.translatable("slugcraft.gourmand_power_two"));
        pTooltipComponents.add(Component.literal(""));
        pTooltipComponents.add(Component.translatable("slugcraft.gourmand_haiku_one"));
        pTooltipComponents.add(Component.translatable("slugcraft.gourmand_haiku_two"));
        pTooltipComponents.add(Component.translatable("slugcraft.gourmand_haiku_three"));
    }
}
