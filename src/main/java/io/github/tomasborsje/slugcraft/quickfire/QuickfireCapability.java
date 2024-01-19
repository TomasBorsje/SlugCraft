package io.github.tomasborsje.slugcraft.quickfire;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.SlugCraftConfig;
import io.github.tomasborsje.slugcraft.core.Registration;
import io.github.tomasborsje.slugcraft.items.GourmandSoul;
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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

import static io.github.tomasborsje.slugcraft.core.Registration.SLUGCAT_SOULS;

public class QuickfireCapability implements IQuickfireCapability {
    public static boolean isRoundRunning = false;
    public static boolean isPreRoundRunning = false;
    public static int roundTime = 0;
    public static int preRoundTime = 0;
    public static int trueTicks = 0;
    public static int preRoundDisplayTime = 2;
    public static float roundTimeMultiplier = 1.0f;
    public final static HashMap<Player, Integer> resistanceLevels = new HashMap<>();
    public final static int THREAT_MUSIC_TIME = 15;
    public final static HashMap<Player, Integer> karmaLevels = new HashMap<>();
    public final static HashMap<Player, Integer> remainingThreatMusicTicks = new HashMap<>();
    private final static UUID rotLevelUUID = UUID.fromString("f186b657-e16b-448f-ad45-37186ee858e8");
    private final static int TICKS_PER_HUNTER_ROT = 20 * 60 * 3; // 1 minute
    private final static int TICKS_PER_SPEARMASTER_NEEDLE = 20 * 15;
    private final static HashMap<Player, Integer> gourmandEatTimers = new HashMap<>();
    private final static HashMap<Player, Integer> gourmandEatCounts = new HashMap<>();
    private final Random random = new Random();
    private StartPoint startPoint;

    /**
     * Executes a text command server-side.
     */
    private void executeCommand(Level level, String command) {
        MinecraftServer server = level.getServer();
        if (server != null) {
            server.getGameRules().getRule(GameRules.RULE_SENDCOMMANDFEEDBACK).set(false, server);
            server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
        }
    }

    private void resetPlayer(ServerPlayer player) {
        // Clear effects and inventory
        player.removeAllEffects();
        player.getInventory().clearContent();

        player.getAbilities().mayfly = false;
        player.onUpdateAbilities();
        player.getFoodData().setFoodLevel(20);

        // Remove the max health modifier from Rotting
        if(player.getAttribute(Attributes.MAX_HEALTH) != null) {
            player.getAttribute(Attributes.MAX_HEALTH).removeModifier(rotLevelUUID);
        }

        // Set player to max hp
        player.setHealth(player.getMaxHealth());

        // Remove any rot they may have
        player.getPersistentData().putInt("rotLevel", 0);
    }

    @Override
    public void startRound(ServerLevel level) {
        roundTime = 0;
        trueTicks = 0;

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
        level.setWeatherParameters(0, 6000000, false, false);

        PacketHandler.sendToAll(new QuickfireRoundStartPacket());

        broadcastMessage(level, Component.translatable("message.slugcraft.quickfire.round_start"));
        broadcastMessage(level, Component.literal(""));

        // For each player
        level.getServer().getPlayerList().getPlayers().forEach(player -> {
            // Clear effects and inventory
            resetPlayer(player);

            // Give a random Slugcat soul into the offhand
            Item slugcatSoul = SLUGCAT_SOULS[random.nextInt(SLUGCAT_SOULS.length)].get();
            ItemStack stack = new ItemStack(slugcatSoul);
            player.getInventory().setItem(Inventory.SLOT_OFFHAND, stack);
            // If the slugcat soul is enot, give the player a singularity bomb
            if(slugcatSoul == Registration.ENOT_SOUL.get()) {
                player.getInventory().setItem(player.getInventory().selected, new ItemStack(Registration.SINGULARITY_GRENADE.get()));
            }
            // Else if they are hunter, give them a spear in selected slot
            else if(slugcatSoul == Registration.HUNTER_SOUL.get()) {
                player.getInventory().setItem(player.getInventory().selected, new ItemStack(Registration.SPEAR.get()));
            }

            // Broadcast their name and soul to all players
            broadcastMessage(level, Component.literal(player.getName().getString() + " - " + stack.getHoverName().getString()));

            // Set player's subtitle to their current soul
            executeCommand(level, "/title " + player.getName().getString() + " subtitle \""+Component.translatable(slugcatSoul.getDescriptionId()).getString()+"\"");
        });


        // Set world difficulty to peaceful
        executeCommand(level, "/difficulty peaceful");
        // Delete all item entities
        executeCommand(level, "/kill @e[type=item]");
        // Set all player gamemodes to survival
        executeCommand(level, "/gamemode survival @a");

        // Give all players resistance equal to their value in resistanceLevels
        resistanceLevels.forEach((player, resistanceLevel) -> {
            if(resistanceLevel > 0) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, resistanceLevel));
                // Send the player a message telling them their resistance level
                player.sendSystemMessage(Component.literal("§3You gain " + resistanceLevel * 20 + "% damage resistance this round!"));
            }
        });

        // Set world border to center on player
        WorldBorder border = level.getWorldBorder();
        border.setDamageSafeZone(1);
        border.setDamagePerBlock(0.2);
        // Set size to max
        border.setSize(SlugCraftConfig.worldBorderStartSize);
        setRoundRunning(true);
    }

    @Override
    public void startPreRound(ServerLevel level, ServerPlayer player) {
        isPreRoundRunning = true;
        preRoundTime = 0;
        preRoundDisplayTime = 2;

        // Clear weather and set difficulty to peaceful
        executeCommand(level, "/weather clear 2000s");
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
        // Reset all players
        level.getServer().getPlayerList().getPlayers().forEach(this::resetPlayer);
    }

    private boolean isAlive(ServerPlayer player) {
        // Check the player is in survival and alive
        return player.gameMode.isSurvival() && player.isAlive();
    }

    @Override
    public void tickWorld(ServerLevel level) {
        trueTicks++;
        // Stop threat music every 30 ticks if the round is not running
        if(trueTicks % 30 == 0 && !isPreRoundRunning && !isRoundRunning) {
            PacketHandler.sendToAll(new StopThreatMusicPacket());
        }

        if (isPreRoundRunning) {
            // Increment preRoundTime;
            preRoundTime++;

            // Every second, show a random map cosmetically
            if (preRoundTime % preRoundDisplayTime == 0 && preRoundDisplayTime < 28) {
                preRoundDisplayTime += 2;
                preRoundTime = 0;

                // If this is the last iteration, select a random NEW map, otherwise random cosmetic map
                if (preRoundDisplayTime + 4 == 28) {
                    startPoint = StartPoints.getRandomNewStartPoint();
                    PacketHandler.sendToAll(new PlayClientsideSoundPacket(SoundEvents.EXPERIENCE_ORB_PICKUP.getLocation()));
                } else if (preRoundDisplayTime + 4 < 28){
                    startPoint = StartPoints.getRandomStartPoint();
                    PacketHandler.sendToAll(new PlayClientsideSoundPacket(SoundEvents.EXPERIENCE_ORB_PICKUP.getLocation()));
                }
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
                executeCommand(level, "/spreadplayers " + startPoint.x + " " + startPoint.z + " " + 0 + " " + startPoint.spreadRadius/2 + " false @a");
                // Set all players gamemodes to adventure
                executeCommand(level, "/gamemode adventure @a");
            }
            if(preRoundTime == 30*20) {
                // Set round time multiplier
                roundTimeMultiplier = startPoint.roundTimeMultiplier;
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
                            // Play bee sting sound to the player
                            PacketHandler.sendToClient(new PlayClientsideSoundPacket(SoundEvents.BEE_STING.getLocation()), player);
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
                                    while(true) {
                                        // Get a random ResourceLocation from the list of craftables
                                        ResourceLocation itemKey = GourmandSoul.CRAFTABLES.get(random.nextInt(GourmandSoul.CRAFTABLES.size()));
                                        // Check the item is registered
                                        if(!ForgeRegistries.ITEMS.containsKey(itemKey)) {
                                            continue;
                                        }

                                        // Give the player the item
                                        //noinspection DataFlowIssue - Suppressed as we use containsKey() to check for existence
                                        ItemStack craftedItemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(itemKey));
                                        player.getInventory().setItem(player.getInventory().selected, craftedItemStack);
                                        // Send a message to the player
                                        player.sendSystemMessage(Component.translatable("message.slugcraft.gourmand.item_craft", craftedItemStack.getHoverName().getString()));
                                        break;
                                    }
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

    private void doProximityThreatMusic(ServerLevel level) {
        // If grace period is still active, return
        if(roundTime < SlugCraftConfig.quickfireGracePeriodTime*20*roundTimeMultiplier) {
            return;
        }

//        // For each player, check if they are closer than 10 blocks to another player
        List<ServerPlayer> players = level.getServer().getPlayerList().getPlayers();
//        for(ServerPlayer player : players) {
//            for(ServerPlayer otherPlayer : players) {
//                float squareXZDistance = (float) (player.getX() - otherPlayer.getX()) * (float) (player.getX() - otherPlayer.getX()) + (float) (player.getZ() - otherPlayer.getZ()) * (float) (player.getZ() - otherPlayer.getZ());
//                if(player != otherPlayer && squareXZDistance < THREAT_MUSIC_DISTANCE * THREAT_MUSIC_DISTANCE) {
//                    // If both players are alive, set both their threat music timers to 10 seconds
//                    if(isAlive(player) && isAlive(otherPlayer)) {
//                        // If the player's don't have threat music playing, start it
//                        ResourceLocation songKey = StartThreatMusicPacket.randomThreat();
//                        if(remainingThreatMusicTicks.getOrDefault(player, 0) <= 0) {
//                            PacketHandler.sendToClient(new StartThreatMusicPacket(songKey), player);
//                        }
//                        if(remainingThreatMusicTicks.getOrDefault(otherPlayer, 0) <= 0) {
//                            PacketHandler.sendToClient(new StartThreatMusicPacket(songKey), otherPlayer);
//                        }
//                        remainingThreatMusicTicks.put(player, THREAT_MUSIC_TIME*20);
//                        remainingThreatMusicTicks.put(otherPlayer, THREAT_MUSIC_TIME*20);
//                        //SlugCraft.LOGGER.info("Setting threat music timers to "+THREAT_MUSIC_TIME+" seconds for players " + player.getName().getString() + " and " + otherPlayer.getName().getString());
//                    }
//                }
//            }
//        }

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
        if (roundTime == (int)(SlugCraftConfig.quickfireGracePeriodTime * 20 * roundTimeMultiplier)) {
            executeCommand(level, "/difficulty normal");
            // Show all players a title
            executeCommand(level, "/title @a title \"§cGrace period over!\"");
        }
        // If we have more than 5 minutes left
        if (roundTime % (int)(20*120*roundTimeMultiplier) == 0 && roundTime < (int)((SlugCraftConfig.quickfireRoundTime * 20 - (5 * 60 * 20))*roundTimeMultiplier)) {
            // Every 2 minutes, give everyone a bonus chest and send them a message
            executeCommand(level, "/give @a chest{BlockEntityTag:{LootTable:\"minecraft:chests/village/village_weaponsmith\"},display:{Name:\"{\\\"text\\\":\\\"Supply Crate\\\",\\\"color\\\":\\\"gold\\\",\\\"bold\\\":true}\"}}");
            broadcastMessage(level, Component.literal("You got a supply crate!").withStyle(Style.EMPTY.withBold(true).withColor(net.minecraft.ChatFormatting.GOLD).withUnderlined(true)));
            // Play xp gain sound to all players with packet
            PacketHandler.sendToAll(new PlayClientsideSoundPacket(SoundEvents.EXPERIENCE_ORB_PICKUP.getLocation()));
        }

        // Check if we have 5 minutes left
        if (roundTime == (int)((SlugCraftConfig.quickfireRoundTime * 20 - (5 * 60 * 20)) * roundTimeMultiplier)) {
            // If so, display a title to all players
            executeCommand(level, "/title @a reset");
            executeCommand(level, "/title @a times 10t 60t 10t");
            executeCommand(level, "/title @a subtitle \"5 minutes left!\"");
            executeCommand(level, "/title @a title \"§cWorld border closing!\"");
            // Start closing the world border to min size
            WorldBorder border = level.getWorldBorder();
            border.lerpSizeBetween(SlugCraftConfig.worldBorderStartSize, SlugCraftConfig.worldBorderEndSize, (long)(5 * 60 * 1000L * roundTimeMultiplier));
            SlugCraft.LOGGER.info("5 minutes left!");
        }
        // If we have no time left, start the rain and threat music
        if (roundTime == (int)(SlugCraftConfig.quickfireRoundTime * 20 * roundTimeMultiplier)) {
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
        if (roundTime == (int)(SlugCraftConfig.quickfireRoundTime*20*roundTimeMultiplier) + SlugCraftConfig.quickfireHardRainDelay*20) {
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
            // Reduce winner's resist level by 2, min 0
                if(resistanceLevels.containsKey(winner)) {
                int resistanceLevel = resistanceLevels.get(winner);
                resistanceLevel -= 2;
                if(resistanceLevel < 0) {
                    resistanceLevel = 0;
                }
                resistanceLevels.put(winner, resistanceLevel);
            }

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
        // Hurt player for 0 damage so health updates
        pLivingEntity.hurt(pLivingEntity.damageSources().generic(), 0.01f);

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
