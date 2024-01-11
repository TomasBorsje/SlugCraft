package io.github.tomasborsje.slugcraft.quickfire;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class QuickfireEvents {
    private static LazyOptional<IQuickfireCapability> quickfireData;
    private static boolean canArtificerDoubleJump = false;
    private static float artificerJumpTolerance = 0;
    private static final List<Item> souls = Arrays.stream(Registration.SLUGCAT_SOULS).map(RegistryObject::get).toList();

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

        event.addCapability(new ResourceLocation(SlugCraft.MODID, "quickfire_handler_cap"), provider);
        SlugCraft.LOGGER.info("Attached Quickfire Level capability to level " + event.getObject().dimension().location());
    }

    @SubscribeEvent
    public static void OnItemsDropped(LivingDropsEvent event) {
        // Disallow any slugcat souls from dropping
        event.getDrops().forEach(entity -> {
            if(souls.contains(entity.getItem().getItem())) {
                // Delete dropped item
                entity.kill();
            }
        });
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

    @SubscribeEvent
    public static void OnEntityHurt(LivingHurtEvent event) {
        // If clientside, return
        if(event.getEntity().level().isClientSide) { return; }

        // If the damager is a player, mark them as not passive
        if(event.getSource().getEntity() instanceof ServerPlayer player) {
            // If they have a saint soul in offhand, send them a message saying they hurt something
            if(player.getInventory().getItem(Inventory.SLOT_OFFHAND).getItem() == Registration.SAINT_SOUL.get()) {
                player.connection.send(new ClientboundClearTitlesPacket(true));
                player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.slugcraft.karma_level.1").withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font")))));
                player.sendSystemMessage(Component.translatable("message.slugcraft.quickfire.saint_hurt").withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font"))));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void OnItemToss(ItemTossEvent event) {
        // If the item is a slugcat soul or a needle, cancel the event
        if(souls.contains(event.getEntity().getItem().getItem()) || event.getEntity().getItem().getItem() == Registration.NEEDLE.get()) {
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
            if(player == null || !player.isAlive() || Minecraft.getInstance().isPaused()) { return; }

            Item offhandItem = player.getInventory().getItem(Inventory.SLOT_OFFHAND).getItem();

            // Check player is holding artificer soul in offhand
            if(offhandItem == Registration.ARTIFICER_SOUL.get() && !player.verticalCollisionBelow && canArtificerDoubleJump && artificerJumpTolerance < 500) {
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
