package io.github.tomasborsje.slugcraft.quickfire;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

public class QuickfireEvents {
    private static LazyOptional<IQuickfireCapability> quickfireData;
    private static boolean canArtificerDoubleJump = false;
    private static float artificerJumpTolerance = 0;
    @SubscribeEvent
    public static void OnAttachLevelCapabilities(AttachCapabilitiesEvent<Level> event) {
        if(!(event.getObject() instanceof ServerLevel)) { return; }

        QuickfireCapability backend = new QuickfireCapability();
        LazyOptional<IQuickfireCapability> optionalStorage = LazyOptional.of(() -> backend);

        ICapabilityProvider provider = new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction) {
                if (cap == Registration.QUICKFIRE_HANDLER) {
                    return optionalStorage.cast();
                }
                return LazyOptional.empty();
            }
        };
        SlugCraft.LOGGER.info("Attached Quickfire Level capability to level " + event.getObject().dimension().location());
        event.addCapability(new ResourceLocation(SlugCraft.MODID, "quickfire_handler_cap"), provider);
    }

    @SubscribeEvent
    public static void OnWorldTick(TickEvent.LevelTickEvent event) {
        if(event.level.dimension() != Level.OVERWORLD) { return; }

        if(event.side == LogicalSide.CLIENT) {
            if(artificerJumpTolerance > 0) {
                artificerJumpTolerance--;
            }
            if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.verticalCollisionBelow) {
                canArtificerDoubleJump = true;
            }
            return;
        }
        // Only tick world events on the server
        ServerLevel level = (ServerLevel) event.level;

        // Get or create capability
        if (quickfireData == null) {
            quickfireData = level.getCapability(Registration.QUICKFIRE_HANDLER);
            quickfireData.addListener(self -> quickfireData = null);
        }

        // If quickfire data exists, run logic
        quickfireData.ifPresent(quickfire -> {
            // Start phase
            if(event.phase == TickEvent.Phase.START) {
                quickfire.tickWorld((ServerLevel) event.level);
            }

            // End phase
            if(event.phase == TickEvent.Phase.END) {

            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void OnItemToss(ItemTossEvent event) {
        // If the item is a slugcat soul, cancel the event
        if(event.getEntity().getItem().getItem() == Registration.RIVULET_SOUL.get() ||
                event.getEntity().getItem().getItem() == Registration.HUNTER_SOUL.get() ||
                event.getEntity().getItem().getItem() == Registration.SPEARMASTER_SOUL.get()) {
            // Send message to player
            event.getPlayer().sendSystemMessage(Component.translatable("message.slugcraft.quickfire.no_toss"));
            event.getPlayer().getInventory().setItem(Inventory.SLOT_OFFHAND, event.getEntity().getItem());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void OnKeyPress(InputEvent.Key event) {
        // Check if the space bar was pressed
        if(event.getKey() == 32 && event.getAction() == InputConstants.PRESS) {
            LocalPlayer player = Minecraft.getInstance().player;
            // If the player is not null, alive, and grounded
            if(player != null && player.isAlive() && !player.verticalCollisionBelow && canArtificerDoubleJump && artificerJumpTolerance < 500) {
                canArtificerDoubleJump = false;

                // Set player velocity in the direction of their look vector
                player.setDeltaMovement(player.getLookAngle().multiply(1.5, 1.5, 1.5));
                // Create an explosion at the player's feet
                player.level().explode(player, player.getX(), player.getY()-0.5f, player.getZ(), 1.5f, Level.ExplosionInteraction.NONE);
                // Increase the player's jump tolerance and damage them if it is too high
                artificerJumpTolerance += 100;
            }
        }
    }
}
