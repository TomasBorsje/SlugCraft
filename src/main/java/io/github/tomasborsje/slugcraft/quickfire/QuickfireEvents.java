package io.github.tomasborsje.slugcraft.quickfire;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class QuickfireEvents {
    private static LazyOptional<IQuickfireCapability> quickfireData;
    private static final Random random = new Random();
    private static boolean canArtificerDoubleJump = false;
    private static float artificerJumpTolerance = 0;
    private static final List<Item> souls = Arrays.stream(Registration.SLUGCAT_SOULS).map(RegistryObject::get).toList();
    public static final Set<BlockPos> replacedChests = new java.util.HashSet<>();

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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void OnPlayerDie(LivingDeathEvent event) {
        // Only run serverside
        if(event.getEntity().level().isClientSide) { return; }

        // Get quickfire capability of the level
        LazyOptional<IQuickfireCapability> quickfireData = event.getEntity().level().getCapability(Registration.QUICKFIRE_HANDLER);

        // If a player died and the round is active, set their gamemode to spectator
        if(event.getEntity() instanceof ServerPlayer player) {
            quickfireData.ifPresent(quickfire -> {
                if(quickfire.getRoundRunning()) {
                    player.setGameMode(GameType.SPECTATOR);
                    player.setHealth(20);
                    event.setCanceled(true);
                    // Broadcast message to all players that the player died
                    QuickfireCapability.broadcastMessage((ServerLevel) event.getEntity().level(), Component.translatable("message.slugcraft.quickfire.player_died", event.getEntity().getDisplayName()));
                }
            });
        }
    }

    @SubscribeEvent
    public static void OnEntityHurt(LivingHurtEvent event) {
        // If clientside, return
        if(event.getEntity().level().isClientSide) { return; }

        // If the damager is a player, mark them as not passive
        if(event.getSource().getEntity() instanceof ServerPlayer player) {
            // If they have a saint soul in offhand and their karma is less than 10
            if(player.getInventory().getItem(Inventory.SLOT_OFFHAND).getItem() == Registration.SAINT_SOUL.get()
                    && QuickfireCapability.karmaLevels.containsKey(player)
                    && QuickfireCapability.karmaLevels.get(player) < 10) {
                player.connection.send(new ClientboundClearTitlesPacket(true));
                player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.slugcraft.karma_level.1").withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font")))));
                player.sendSystemMessage(Component.translatable("message.slugcraft.quickfire.saint_hurt").withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font"))));
            }
        }
    }

    @SubscribeEvent
    public static void OnPlayerPlaceBlock(final BlockEvent.EntityPlaceEvent event) {
        // If a chest was placed, add it to the list of replaced chests
        // This prevents players placing chests to generate loot
        if(event.getPlacedBlock().getBlock() == Blocks.CHEST) {
            replacedChests.add(event.getPos());
        }
    }

    @SubscribeEvent
    public static void OnPlayerInteractChest(final PlayerInteractEvent event) {
        // If clientside, return
        if(event.getLevel().isClientSide) { return; }

        // Get the blockState that was interacted with
        ServerLevel level = (ServerLevel) event.getLevel();
        final var blockState = level.getBlockState(event.getPos());
        // If it was a chest
        if(blockState.is(Blocks.CHEST)) {
            // Get the quickfire capability of the level
            LazyOptional<IQuickfireCapability> quickfireData = level.getCapability(Registration.QUICKFIRE_HANDLER);

            // If the round is running, cancel the event
            quickfireData.ifPresent(quickfire -> {
                if(quickfire.getRoundRunning() && !replacedChests.contains(event.getPos())) {
                    // Replace the chest with a new chest that has a loot table
                    replaceChest(level, event.getPos());
                    // Replace all adjacent chests
                    for(Direction direction : Direction.values()) {
                        BlockPos pos = event.getPos().relative(direction);
                        if(level.getBlockState(pos).is(Blocks.CHEST)) {
                            replaceChest(level, pos);
                        }
                    }
                }
            });
        }
    }

    static void replaceChest(ServerLevel level, BlockPos pos) {
        replacedChests.add(pos);
        // Replace the chest with a new chest that has a loot table
        ChestBlockEntity newChest = new ChestBlockEntity(pos, Blocks.CHEST.defaultBlockState());
        newChest.setLootTable(new ResourceLocation("minecraft", "chests/end_city_treasure"), random.nextLong());
        // Add the new chest to the level
        level.setBlockEntity(newChest);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void OnItemToss(ItemTossEvent event) {
        // If the item is a slugcat soul or a needle, cancel the event
        if(souls.contains(event.getEntity().getItem().getItem())) {
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
