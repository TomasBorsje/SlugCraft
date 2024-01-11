package io.github.tomasborsje.slugcraft.quickfire;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.SlugCraftConfig;
import io.github.tomasborsje.slugcraft.core.Registration;
import io.github.tomasborsje.slugcraft.network.HardRainStartPacket;
import io.github.tomasborsje.slugcraft.network.PacketHandler;
import io.github.tomasborsje.slugcraft.network.QuickfireRoundStartPacket;
import io.github.tomasborsje.slugcraft.network.PlayClientsideSound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static io.github.tomasborsje.slugcraft.core.Registration.SLUGCAT_SOULS;

public class QuickfireCapability implements IQuickfireCapability {
    public boolean isRoundRunning = false;
    private final static UUID rotLevelUUID = UUID.fromString("f186b657-e16b-448f-ad45-37186ee858e8");
    private final static int TICKS_PER_HUNTER_ROT = 20 * 60; // 1 minute
    private final static int TICKS_PER_SPEARMASTER_NEEDLE = 20 * 30;
    private final static HashMap<Player, Integer> gourmandEatTimers = new HashMap<>();
    private final static HashMap<Player, Integer> gourmandEatCounts = new HashMap<>();
    private final static HashMap<Player, Integer> karmaLevels = new HashMap<>();
    private final Random random = new Random();
    private int roundTime = 0;

    // Use reflection to get the getChunks() method of ChunkMap
    Method getChunks;
    {
        try {
            getChunks = ChunkMap.class.getDeclaredMethod("getChunks");
            getChunks.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a text command server-side.
     */
    private void executeCommand(Level level, String command) {
        MinecraftServer server = level.getServer();
        server.getGameRules().getRule(GameRules.RULE_SENDCOMMANDFEEDBACK).set(false, server);
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
    }

    @Override
    public void startRound(ServerLevel level, Player startPlayer) {
        roundTime = 0;

        // Empty all round data
        gourmandEatTimers.clear();
        gourmandEatCounts.clear();
        karmaLevels.clear();

        // Show round prep title to all players
        executeCommand(level, "/title @a times 10t 40t 10t");
        executeCommand(level, "/title @a title \"Go!\"");

        PacketHandler.sendToAll(new QuickfireRoundStartPacket());

        // For each player
        level.getServer().getPlayerList().getPlayers().forEach(player -> {

            // Clear effects and inventory
            player.removeAllEffects();
            player.getInventory().clearContent();
            // Give a random Slugcat soul into the offhand
            Item slugcatSoul = SLUGCAT_SOULS[random.nextInt(SLUGCAT_SOULS.length)].get();
            player.getInventory().setItem(Inventory.SLOT_OFFHAND, new ItemStack(slugcatSoul));

            // Set player's subtitle to their current soul
            executeCommand(level, "/title " + player.getName().getString() + " subtitle \""+Component.translatable(slugcatSoul.getDescriptionId()).getString()+"\"");
            // Send a message
            player.sendSystemMessage(Component.translatable("message.slugcraft.quickfire.round_start"));
        });

        // Call getChunks() on the ChunkMap using reflection
        ChunkMap chunkMap = level.getChunkSource().chunkMap;
        Iterable<ChunkHolder> chunks;
        try {
            chunks = (Iterable<ChunkHolder>) getChunks.invoke(chunkMap);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        // For each chunk, replace all chests with diamond blocks
        for (ChunkHolder chunkHolder : chunks) {
            // Check if it exists, and get the LevelChunk through getTickingChunk if so:
            if (chunkHolder != null) {
                LevelChunk chunk = chunkHolder.getTickingChunk();
                if (chunk == null) continue;
                // Replace all chests with diamond blocks
                for (BlockPos pos : chunk.getBlockEntitiesPos()) {
                    if (level.getBlockState(pos).is(Blocks.CHEST)) {
                        level.setBlockAndUpdate(pos, Blocks.DIAMOND_BLOCK.defaultBlockState());
                    }
                }
            }
        }

        // Set world difficulty to peaceful then back to normal
        executeCommand(level, "/difficulty peaceful");
        executeCommand(level, "/difficulty normal");
        // Delete all item entities
        executeCommand(level, "/kill @e[type=item]");
        // Set all player gamemodes to survival
        executeCommand(level, "/gamemode survival @a");

        // Set world border to center on player
        WorldBorder border = level.getWorldBorder();
        border.setCenter(startPlayer.getX(), startPlayer.getZ());
        border.lerpSizeBetween(SlugCraftConfig.worldBorderStartSize, SlugCraftConfig.worldBorderEndSize, SlugCraftConfig.quickfireRoundTime * 1000L);
        border.setDamageSafeZone(1);
        border.setDamagePerBlock(0.2);
        setRoundRunning(true);
    }

    @Override
    public void tickWorld(ServerLevel level) {
        // If the round is running
        if (isRoundRunning) {
            // Check if there is only 1 player alive
            List<ServerPlayer> livingPlayers = new ArrayList<>();
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                if (player.isAlive()) {
                    livingPlayers.add(player);
                }
            }
            // If only 1 living player remains, end the round
            if (livingPlayers.size() == 2) {
                // Show round end title to all players
                executeCommand(level, "/title @a title \"Round over!\"");
                executeCommand(level, "/title @a subtitle \"" + livingPlayers.get(0).getName().getString() + " wins!\"");

                endRound(level, livingPlayers.get(0));
                return;
            }

            doTimedEvents(level);

            // Increment tickCount;
            roundTime++;

            // For each player, check their offhand souls
            for (ServerPlayer player : livingPlayers) {
                ItemStack offhand = player.getInventory().getItem(Inventory.SLOT_OFFHAND);

                // Check for rotting players
                if (roundTime % TICKS_PER_HUNTER_ROT == 0 && player.hasEffect(Registration.ROTTING.get()) && player.isAlive()) {
                    // If so, rot the player
                    rotPlayer(player);
                }

                // Artificer
                if (offhand.getItem() == Registration.ARTIFICER_SOUL.get()) {
                    // Use for loop to iterate
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (stack.getItem() == Registration.SPEAR.get()) {
                            player.getInventory().setItem(i, new ItemStack(Registration.EXPLOSIVE_SPEAR.get()));
                        }
                    }
                // Spearmaster
                } else if (offhand.getItem() == Registration.SPEARMASTER_SOUL.get()) {
                    if (roundTime % TICKS_PER_SPEARMASTER_NEEDLE == 0) {
                        // Check the player only has 2 or fewer needles
                        int needleCount = 0;
                        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                            ItemStack stack = player.getInventory().getItem(i);
                            if (stack.getItem() == Registration.NEEDLE.get()) {
                                needleCount++;
                            }
                        }
                        if (needleCount <= 2) {
                            // Otherwise, give the player a needle
                            player.getInventory().add(new ItemStack(Registration.NEEDLE.get()));
                            // Send a message to the player
                            player.sendSystemMessage(Component.translatable("message.slugcraft.quickfire.needle_gained"));
                        }
                    }
                }
                // Saint
                if (offhand.getItem() == Registration.SAINT_SOUL.get()) {
                    // If the number of seconds has passed and the player is passive
                    if (roundTime % (SlugCraftConfig.saintSecondsPerKarmaLevel * 20) == 0) {
                        // Increment the player's karma level
                        if (!karmaLevels.containsKey(player)) {
                            karmaLevels.put(player, 0);
                        }
                        int karmaLevel = karmaLevels.get(player);

                        // if the player is below level 10, increment their karma level
                        if (karmaLevel < 10) {
                            karmaLevels.put(player, ++karmaLevel);

                            // Send play sound packet to play gain_karma sound
                            PacketHandler.sendToAll(new PlayClientsideSound(Registration.GAIN_KARMA.getId()));

                            // If less than level 10, show a karma level title to the player
                            if(karmaLevel < 10) {
                                player.connection.send(new ClientboundClearTitlesPacket(true));
                                player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.slugcraft.karma_level." + karmaLevel).withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font")))));
                                player.sendSystemMessage(Component.translatable("message.slugcraft.karma_level." + karmaLevel).withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font"))));
                            }
                            // Otherwise, if the player is at level 10, send them a message saying they've ascended
                            else {
                                player.connection.send(new ClientboundClearTitlesPacket(true));
                                player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.slugcraft.karma_level.10").withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font")))));
                                level.getServer().sendSystemMessage(Component.translatable("message.slugcraft.quickfire.player_ascend", player.getName().getString()).withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font"))));
                            }
                        }
                    }

                }
                // Gourmand
                if (offhand.getItem() == Registration.GOURMAND_SOUL.get()) {
                    // If the player is crouching, increment their gourmand timer by 1
                    ItemStack heldItem = player.getInventory().getSelected();
                    if (player.isCrouching() && player.isAlive() && heldItem != ItemStack.EMPTY && heldItem.getItem().getMaxStackSize(heldItem) == 1) {
                        if (!gourmandEatTimers.containsKey(player)) {
                            gourmandEatTimers.put(player, 0);
                        }
                        int eatTimer = gourmandEatTimers.get(player);
                        gourmandEatTimers.put(player, ++eatTimer);

                        if(eatTimer <= 3 * 20) {
                            // Play the eat sound effect every 5 ticks
                            if (eatTimer % 5 == 0) {
                                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0f, 1.0f);
                            }
                            // Create an eat particle every 3 ticks
                            if (eatTimer % 3 == 0) {
                                level.addParticle(ParticleTypes.CRIMSON_SPORE, player.getX(), player.getY() + player.getEyeHeight() - 0.05f, player.getZ(), 0, 0, 0);
                            }
                            // If the player has eaten for 3 seconds, remove their current held main hand item and give them a spear
                            if (eatTimer == 3 * 20) {
                                // Remove current held item
                                String eatenItemName = player.getInventory().getSelected().getHoverName().getString();
                                player.getInventory().removeItem(player.getInventory().getSelected());
                                // Increment player's eat count by 1
                                if (!gourmandEatCounts.containsKey(player)) {
                                    gourmandEatCounts.put(player, 0);
                                }
                                int eatCount = gourmandEatCounts.get(player);
                                gourmandEatCounts.put(player, ++eatCount);

                                // Send a message to the player
                                player.sendSystemMessage(Component.translatable("message.slugcraft.gourmand.item_eaten", eatenItemName));

                                // If the player has eaten 2 items, reset their eat count and give them a spear
                                if (eatCount == 2) {
                                    gourmandEatCounts.put(player, 0);
                                    ItemStack craftedItem = new ItemStack(Registration.SPEAR.get());
                                    player.getInventory().setItem(player.getInventory().selected, craftedItem);
                                    // Send a message to the player
                                    player.sendSystemMessage(Component.translatable("message.slugcraft.gourmand.item_craft", craftedItem.getHoverName().getString()));
                                }
                            }
                        }
                    } else {
                        // Otherwise, reset their gourmand timer
                        gourmandEatTimers.put(player, 0);
                    }
                }
            }
        }
    }

    private void doTimedEvents(ServerLevel level) {
        // Check if we have 5 minutes left
        if (roundTime == SlugCraftConfig.quickfireRoundTime*20 - 5 * 60 * 20) {
            // If so, display a title to all players
            executeCommand(level, "/title @a reset");
            executeCommand(level, "/title @a title \"5 minutes left!\"");
            SlugCraft.LOGGER.info("5 minutes left!");
        }
        // If we have no time left, start the rain and threat music
        if (roundTime == SlugCraftConfig.quickfireRoundTime*20) {
            SlugCraft.LOGGER.info("No time left, playing threat music and starting rain!");

            executeCommand(level, "/title @a reset");
            executeCommand(level, "/title @a title \"The rain has started...\"");
            // Start rain
            level.setWeatherParameters(0, 6000, true, false);
            // Iterate all players and play threat music
            PacketHandler.sendToAll(new PlayClientsideSound(Registration.THREAT_GARBAGE_WASTES.getId()));
        }
        // If we're 30 seconds past the round limit, apply wither
        if (roundTime == SlugCraftConfig.quickfireRoundTime*20 + 10*20) {
            SlugCraft.LOGGER.info("10 seconds past round limit, starting hard rain + wither!");
            PacketHandler.sendToAll(new HardRainStartPacket());
            for (Player player : level.getServer().getPlayerList().getPlayers()) {
                if (player.isAlive()) {
                    // Add wither and slowness 1
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, -1, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 0));
                }
            }
        }
    }

    public void endRound(ServerLevel level, Player winner) {
        // Set roundRunning to false
        setRoundRunning(false);

        // Set world border to min size
        level.getWorldBorder().setSize(SlugCraftConfig.worldBorderEndSize);

        // Iterate all players and send a round end message
        level.getServer().getPlayerList().getPlayers().forEach(player -> {
            player.sendSystemMessage(Component.translatable("message.slugcraft.quickfire.round_end", winner.getName().getString()));
        });
    }

    @Override
    public void postTickWorld() {

    }

    @Override
    public void rotPlayer(LivingEntity pLivingEntity) {
        SlugCraft.LOGGER.info("Rotting player " + pLivingEntity.getName().getString() + "...");

        // Get or create rotLevel tag
        if (!pLivingEntity.getPersistentData().contains("rotLevel")) {
            pLivingEntity.getPersistentData().putInt("rotLevel", 0);
        }
        // Increment rotLevel
        int rotLevel = pLivingEntity.getPersistentData().getInt("rotLevel");
        rotLevel++;
        pLivingEntity.getPersistentData().putInt("rotLevel", rotLevel);

        // Reduce player's max health by rotLevel*2
        pLivingEntity.getAttribute(Attributes.MAX_HEALTH).removeModifier(rotLevelUUID);
        pLivingEntity.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(new AttributeModifier(rotLevelUUID, "Rotting", -2 * rotLevel, AttributeModifier.Operation.ADDITION));

        // Send ominous message to player
        if (pLivingEntity instanceof Player) {
            Player player = (Player) pLivingEntity;
            player.sendSystemMessage(Component.translatable("message.slugcraft.rotting"));
        }

        // If the player's rot level is 10 or more, apply wither and huge slowness
        if (rotLevel >= 10) {
            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, -1, 1));
            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 3));
            // Send 'you're out of time...' message in dark gray
            if (pLivingEntity instanceof Player) {
                Player player = (Player) pLivingEntity;
                player.sendSystemMessage(Component.translatable("message.slugcraft.rotting_final"));
            }
        }

    }

    @Override
    public boolean getRoundRunning() {
        return isRoundRunning;
    }

    @Override
    public void setRoundRunning(boolean roundRunning) {
        isRoundRunning = roundRunning;
    }
}
