package io.github.tomasborsje.slugcraft.quickfire;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.SlugCraftConfig;
import io.github.tomasborsje.slugcraft.core.Registration;
import io.github.tomasborsje.slugcraft.network.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
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
import net.minecraft.world.level.border.WorldBorder;

import javax.annotation.Nullable;
import java.util.*;

import static io.github.tomasborsje.slugcraft.core.Registration.SLUGCAT_SOULS;

public class QuickfireCapability implements IQuickfireCapability {
    public static boolean isRoundRunning = false;
    public static boolean isPreRoundRunning = false;
    public static int roundTime = 0;
    public static int preRoundTime = 0;
    public static int preRoundDisplayTime = 2;
    public final static float THREAT_MUSIC_DISTANCE = 13.0f;
    public final static int THREAT_MUSIC_TIME = 15;
    public final static HashMap<Player, Integer> karmaLevels = new HashMap<>();
    public final static HashMap<Player, Integer> remainingThreatMusicTicks = new HashMap<>();
    private final static UUID rotLevelUUID = UUID.fromString("f186b657-e16b-448f-ad45-37186ee858e8");
    private final static int TICKS_PER_HUNTER_ROT = 20 * 60; // 1 minute
    private final static int TICKS_PER_SPEARMASTER_NEEDLE = 20 * 30;
    private final static HashMap<Player, Integer> gourmandEatTimers = new HashMap<>();
    private final static HashMap<Player, Integer> gourmandEatCounts = new HashMap<>();
    private final Random random = new Random();
    private boolean lastTickRoundRunning = false;
    private StartPoint startPoint;

    // Use reflection to get the getChunks() method of ChunkMap
//    Method getChunks;
//    {
//        try {
//            getChunks = ChunkMap.class.getDeclaredMethod("getChunks");
//            getChunks.setAccessible(true);
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * Executes a text command server-side.
     */
    private void executeCommand(Level level, String command) {
        MinecraftServer server = level.getServer();
        server.getGameRules().getRule(GameRules.RULE_SENDCOMMANDFEEDBACK).set(false, server);
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
    }

    @Override
    public void startRound(ServerLevel level) {
        roundTime = 0;

        // Empty all round data
        gourmandEatTimers.clear();
        gourmandEatCounts.clear();
        karmaLevels.clear();
        QuickfireEvents.replacedChests.clear();
        remainingThreatMusicTicks.clear();

        // Show round prep title to all players
        executeCommand(level, "/title @a times 10t 40t 10t");
        executeCommand(level, "/title @a title \"Go!\"");

        // Set weather to clear
        level.setWeatherParameters(0, 6000, false, false);

        PacketHandler.sendToAll(new QuickfireRoundStartPacket());

        // For each player
        level.getServer().getPlayerList().getPlayers().forEach(player -> {
            // Clear effects and inventory
            player.removeAllEffects();
            player.getInventory().clearContent();

            player.getAbilities().mayfly = false;
            player.onUpdateAbilities();

            // Remove the max health modifier from Rotting
            if(player.getAttribute(Attributes.MAX_HEALTH) != null) {
                player.getAttribute(Attributes.MAX_HEALTH).removeModifier(rotLevelUUID);
            }

            // Give a random Slugcat soul into the offhand
            Item slugcatSoul = SLUGCAT_SOULS[random.nextInt(SLUGCAT_SOULS.length)].get();
            player.getInventory().setItem(Inventory.SLOT_OFFHAND, new ItemStack(slugcatSoul));
            // If the slugcat soul is enot, give the player a singularity bomb
            if(slugcatSoul == Registration.ENOT_SOUL.get()) {
                player.getInventory().setItem(player.getInventory().selected, new ItemStack(Registration.SINGULARITY_GRENADE.get()));
            }
            // Else if they are hunter, give them a spear in selected slot
            else if(slugcatSoul == Registration.HUNTER_SOUL.get()) {
                player.getInventory().setItem(player.getInventory().selected, new ItemStack(Registration.SPEAR.get()));
            }

            // Set player's subtitle to their current soul
            executeCommand(level, "/title " + player.getName().getString() + " subtitle \""+Component.translatable(slugcatSoul.getDescriptionId()).getString()+"\"");
            // Send a message
            player.sendSystemMessage(Component.translatable("message.slugcraft.quickfire.round_start"));
        });

        // Set world difficulty to peaceful
        executeCommand(level, "/difficulty peaceful");
        // Delete all item entities
        executeCommand(level, "/kill @e[type=item]");
        // Set all player gamemodes to survival
        executeCommand(level, "/gamemode survival @a");

        // Set world border to center on player
        WorldBorder border = level.getWorldBorder();
        border.lerpSizeBetween(SlugCraftConfig.worldBorderStartSize, SlugCraftConfig.worldBorderEndSize, SlugCraftConfig.quickfireRoundTime * 1000L);
        border.setDamageSafeZone(1);
        border.setDamagePerBlock(0.2);
        setRoundRunning(true);
    }

    @Override
    public void startPreRound(ServerLevel level, ServerPlayer player) {
        isPreRoundRunning = true;
        preRoundTime = 0;
        preRoundDisplayTime = 2;

        // Clear weather and set difficulty to peaceful
        level.setWeatherParameters(0, 6000, false, false);
        executeCommand(level, "/difficulty peaceful");
        // Set all player gamemodes to spectator
        executeCommand(level, "/gamemode spectator @a");
        // Set all player title times to 0t 80t 20t
        executeCommand(level, "/title @a times 0t 80t 20t");
        // Set time to day
        executeCommand(level, "/time set day");
        // Clear all player effects
        executeCommand(level, "/effect clear @a");
        // Clear all player inventories
        executeCommand(level, "/clear @a");
        // Set all player's health to 20
        executeCommand(level, "/effect give @a minecraft:instant_health 1 19");
        // Set all player's hunger to 20
        executeCommand(level, "/effect give @a minecraft:saturation 1 19");
    }

    private boolean isAlive(ServerPlayer player) {
        // Check the player is in survival and alive
        return player.gameMode.isSurvival() && player.isAlive();
    }

    @Override
    public void tickWorld(ServerLevel level) {
        // If the round was running last tick but not this tick, end all threat musics again
        // This stops threat music that started on the same tick the round ended
        if(lastTickRoundRunning && !isRoundRunning) {
            PacketHandler.sendToAll(new StopThreatMusicPacket());
        }

        if (isPreRoundRunning) {
            // Increment preRoundTime;
            preRoundTime++;

            SlugCraft.LOGGER.info("Pre-round time: " + preRoundTime + " display = " + preRoundDisplayTime);

            // Every second, show a random map cosmetically
            if (preRoundTime % preRoundDisplayTime == 0 && preRoundDisplayTime < 28) {
                preRoundDisplayTime += 2;
                preRoundTime = 0;
                // Play an xp pickup sound to all players
                executeCommand(level, "/playsound minecraft:entity.experience_orb.pickup master @a");

                // Get a random start point
                startPoint = StartPoints.getRandomStartPoint();
                // Show a title to all players
                executeCommand(level, "/title @a subtitle \"§eChoosing map...\"");
                executeCommand(level, "/title @a title \"" + startPoint.name + "\"");
            }
            else if(preRoundDisplayTime == 28) {
                SlugCraft.LOGGER.info("Starting round on map " + startPoint.name);
                preRoundDisplayTime++;
                // Play karma gain sound to all players with packet
                PacketHandler.sendToAll(new PlayClientsideSoundPacket(Registration.GAIN_KARMA.getId()));
                // Select final map and set up round;
                // Set world spawn to start point
                level.setDefaultSpawnPos(new BlockPos(startPoint.x, 64, startPoint.z), 0.0f);
                // Set world border to center on start point
                WorldBorder border = level.getWorldBorder();
                border.setCenter(startPoint.x, startPoint.z);
                border.setSize(startPoint.spreadRadius*2);
                // Show a title to all players, with a 5-second duration this time
                executeCommand(level, "/title @a times 0t 150t 20t");
                executeCommand(level, "/title @a subtitle \"§eStarting in 30 seconds!\"");
                executeCommand(level, "/title @a title \"" + startPoint.name + "\"");
                // Spreadplayers around worldborder
                executeCommand(level, "/spreadplayers " + startPoint.x + " " + startPoint.z + " " + 0 + " " + startPoint.spreadRadius + " false @a");
                // Set all players gamemodes to adventure
                executeCommand(level, "/gamemode adventure @a");
            }
            if(preRoundTime == 30*20) {
                // Start round
                startRound(level);
                isPreRoundRunning = false;
                return;
            }
        }
        // If the round is running
        if (isRoundRunning) {
            // Check if there is only 1 player alive
            List<ServerPlayer> livingPlayers = new ArrayList<>();
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                if (isAlive(player)) {
                    livingPlayers.add(player);
                }
            }
            // If only 1 living player remains, end the round
            if (livingPlayers.size() == 1 || livingPlayers.isEmpty()) {
                // Show round end title to all players
                if(!livingPlayers.isEmpty()) {
                    executeCommand(level, "/title @a subtitle \"" + livingPlayers.get(0).getName().getString() + " wins!\"");
                }
                executeCommand(level, "/title @a title \"GG!\"");
                endRound(level, livingPlayers.isEmpty() ? null : livingPlayers.get(0));
                return;
            }

            // Increment tickCount;
            roundTime++;
            doTimedEvents(level);
            doProximityThreatMusic(level);

            // For each player, check their offhand souls
            for (ServerPlayer player : livingPlayers) {
                ItemStack offhand = player.getInventory().getItem(Inventory.SLOT_OFFHAND);

                // Check for rotting players
                if (roundTime % TICKS_PER_HUNTER_ROT == 0 && player.hasEffect(Registration.ROTTING.get()) && isAlive(player)) {
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
                    if (roundTime % TICKS_PER_SPEARMASTER_NEEDLE == 0 && isAlive(player)) {
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
                            PacketHandler.sendToAll(new PlayClientsideSoundPacket(Registration.GAIN_KARMA.getId()));

                            // If less than level 10, show a karma level title to the player
                            if(karmaLevel < 10) {
                                player.connection.send(new ClientboundClearTitlesPacket(true));
                                player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.slugcraft.karma_level." + karmaLevel).withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font")))));
                                player.sendSystemMessage(Component.translatable("message.slugcraft.karma_level." + karmaLevel).withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font"))));
                            }
                            // Otherwise, if the player is at level 10, send them a message saying they've ascended
                            else {
                                // Grant creative flight
                                player.getAbilities().mayfly = true;
                                player.onUpdateAbilities();
                                // Grant regeneration
                                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 0));

                                player.connection.send(new ClientboundClearTitlesPacket(true));
                                player.connection.send(new ClientboundSetSubtitleTextPacket(Component.translatable("message.slugcraft.quickfire.saint_ascend")));
                                player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("message.slugcraft.karma_level.10").withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font")))));
                                broadcastMessage(level, Component.translatable("message.slugcraft.quickfire.player_ascend", player.getName().getString()).withStyle(Style.EMPTY.withFont(new ResourceLocation(SlugCraft.MODID, "slugcraft_font"))));
                            }
                        }
                    }
                }
                // Gourmand
                if (offhand.getItem() == Registration.GOURMAND_SOUL.get()) {
                    // If the player is crouching, increment their gourmand timer by 1
                    ItemStack heldItem = player.getInventory().getSelected();
                    if (player.isCrouching() && isAlive(player) && heldItem != ItemStack.EMPTY && heldItem.getItem().getMaxStackSize(heldItem) == 1) {
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
                            // If the player has eaten for 3 seconds, remove their current held main hand item and give them a spear
                            if (eatTimer == 3 * 20) {
                                // Play burp sound
                                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1.0f, 1.0f);
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
        lastTickRoundRunning = isRoundRunning;
    }

    private void doProximityThreatMusic(ServerLevel level) {
        // If grace period is still active, return
        if(roundTime < SlugCraftConfig.quickfireGracePeriodTime*20) {
            return;
        }

        // For each player, check if they are closer than 10 blocks to another player
        List<ServerPlayer> players = level.getServer().getPlayerList().getPlayers();
        for(ServerPlayer player : players) {
            for(ServerPlayer otherPlayer : players) {
                float squareXZDistance = (float) (player.getX() - otherPlayer.getX()) * (float) (player.getX() - otherPlayer.getX()) + (float) (player.getZ() - otherPlayer.getZ()) * (float) (player.getZ() - otherPlayer.getZ());
                if(player != otherPlayer && squareXZDistance < THREAT_MUSIC_DISTANCE * THREAT_MUSIC_DISTANCE) {
                    // If both players are alive, set both their threat music timers to 10 seconds
                    if(isAlive(player) && isAlive(otherPlayer)) {
                        // If the player's don't have threat music playing, start it
                        ResourceLocation songKey = StartThreatMusicPacket.randomThreat();
                        if(remainingThreatMusicTicks.getOrDefault(player, 0) <= 0) {
                            PacketHandler.sendToClient(new StartThreatMusicPacket(songKey), player);
                        }
                        if(remainingThreatMusicTicks.getOrDefault(otherPlayer, 0) <= 0) {
                            PacketHandler.sendToClient(new StartThreatMusicPacket(songKey), otherPlayer);
                        }
                        remainingThreatMusicTicks.put(player, THREAT_MUSIC_TIME*20);
                        remainingThreatMusicTicks.put(otherPlayer, THREAT_MUSIC_TIME*20);
                        //SlugCraft.LOGGER.info("Setting threat music timers to "+THREAT_MUSIC_TIME+" seconds for players " + player.getName().getString() + " and " + otherPlayer.getName().getString());
                    }
                }
            }
        }

        // Decrease all threat music timers by 1
        remainingThreatMusicTicks.replaceAll((k, v) -> v-1);

        // For each player, check if their threat music timer is 0
        for(ServerPlayer player : players) {
            if(remainingThreatMusicTicks.getOrDefault(player, -1) == 0) {
                // If so, stop the threat music
                PacketHandler.sendToClient(new StopThreatMusicPacket(), player);
            }
        }
    }

    public static void broadcastMessage(ServerLevel level, Component message) {
        level.getServer().getPlayerList().getPlayers().forEach(player -> {
            player.sendSystemMessage(message);
        });
    }

    private void doTimedEvents(ServerLevel level) {
        // If we have reached the grace period time, set difficulty to normal again
        if (roundTime == SlugCraftConfig.quickfireGracePeriodTime * 20) {
            executeCommand(level, "/difficulty normal");
            // Show all players a title
            executeCommand(level, "/title @a title \"Grace period over!\"");
        }

        // Check if we have 5 minutes left
        if (roundTime == SlugCraftConfig.quickfireRoundTime * 20 - 5 * 60 * 20) {
            // If so, display a title to all players
            executeCommand(level, "/title @a reset");
            executeCommand(level, "/title @a title \"5 minutes left!\"");
            SlugCraft.LOGGER.info("5 minutes left!");
        }
        // If we have no time left, start the rain and threat music
        if (roundTime == SlugCraftConfig.quickfireRoundTime*20) {
            SlugCraft.LOGGER.info("No time left, playing threat music and starting rain!");
            // Set time to night
            executeCommand(level, "/time set night");

            executeCommand(level, "/title @a reset");
            executeCommand(level, "/title @a times 10t 40t 10t");
            executeCommand(level, "/title @a title \"§7The rain has started...\"");

            // Start rain
            level.setWeatherParameters(0, 6000, true, false);

            // All players that don't already have threat music playing, start it
            ResourceLocation songKey = StartThreatMusicPacket.randomThreat();
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                if (isAlive(player) && remainingThreatMusicTicks.getOrDefault(player, 0) <= 0) {
                    PacketHandler.sendToClient(new StartThreatMusicPacket(songKey), player);
                    remainingThreatMusicTicks.put(player, 180*20);
                }
            }
        }
        // If we're past the hard rain delay, start the hard rain and wither
        if (roundTime == SlugCraftConfig.quickfireRoundTime*20 + SlugCraftConfig.quickfireHardRainDelay*20) {
            SlugCraft.LOGGER.info("10 seconds past round limit, starting hard rain + wither!");
            PacketHandler.sendToAll(new HardRainStartPacket());
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                if (isAlive(player)) {
                    // Add wither and slowness 1
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, -1, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 0));
                }
            }
        }
    }

    public void endRound(ServerLevel level, @Nullable Player winner) {
        // Set roundRunning to false
        setRoundRunning(false);

        // Set world border to min size
        level.getWorldBorder().setSize(SlugCraftConfig.worldBorderStartSize);

        // Turn off all threat music
        PacketHandler.sendToAll(new StopThreatMusicPacket());

        // Iterate all players and send a round end message
        if(winner != null) {
            level.getServer().getPlayerList().getPlayers().forEach(player -> {
                player.sendSystemMessage(Component.translatable("message.slugcraft.quickfire.round_end", winner.getName().getString()));
            });
        }
    }

    @Override
    public void postTickWorld() { }

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
        if (pLivingEntity instanceof Player player) {
            player.sendSystemMessage(Component.translatable("message.slugcraft.rotting"));
        }

        // If the player's rot level is 10 or more, apply wither and huge slowness
        if (rotLevel >= 10) {
            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, -1, 1));
            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 3));
            // Send 'you're out of time...' message in dark gray
            if (pLivingEntity instanceof Player player) {
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
