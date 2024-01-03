package io.github.tomasborsje.slugcraft.quickfire;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.chunk.LevelChunk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.UUID;

public class QuickfireCapability implements IQuickfireCapability {
    public boolean isRoundRunning = false;

    private final static UUID rotLevelUUID = UUID.fromString("f186b657-e16b-448f-ad45-37186ee858e8");
    private final static int TICKS_PER_HUNTER_ROT = 20*60; // 1 minute
    private final static int TICKS_PER_SPEARMASTER_NEEDLE = 20*30;
    private final Random random = new Random();
    private final Item[] SLUGCAT_SOULS = {Registration.HUNTER_SOUL.get(), Registration.RIVULET_SOUL.get(), Registration.SPEARMASTER_SOUL.get()};
    private int tickCount = 0;

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
        // Show round prep title to all players
        executeCommand(level, "/title @a title \"Prepping...\"");

        // For each player
        level.getServer().getPlayerList().getPlayers().forEach(player -> {
            // Clear effects and inventory
            player.removeAllEffects();
            player.getInventory().clearContent();
            // Give a random Slugcat soul into the offhand
            Item slugcatSoul = SLUGCAT_SOULS[random.nextInt(SLUGCAT_SOULS.length)];
            player.getInventory().setItem(Inventory.SLOT_OFFHAND, new ItemStack(slugcatSoul));
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
        for(ChunkHolder chunkHolder : chunks) {
            // Check if it exists, and get the LevelChunk through getTickingChunk if so:
            if(chunkHolder != null) {
                LevelChunk chunk = chunkHolder.getTickingChunk();
                if(chunk == null) continue;
                // Replace all chests with diamond blocks
                for(BlockPos pos : chunk.getBlockEntitiesPos()) {
                    if(level.getBlockState(pos).is(Blocks.CHEST)) {
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

        // Show round prep title to all players
        executeCommand(level, "/title @a title \"Round starting!\"");

        setRoundRunning(true);
    }

    @Override
    public void tickWorld(ServerLevel level) {
        // If the round is running
        if(isRoundRunning) {
            // Increment tickCount
            SlugCraft.LOGGER.info("Tick count: "+tickCount);
            tickCount++;
            // If tickCount is a multiple of TICKS_PER_HUNTER_ROT
            if(tickCount % TICKS_PER_HUNTER_ROT == 0) {
                // For each player
                for(Player player : level.getServer().getPlayerList().getPlayers()) {
                    // Check if the player has the 'Rotting' effect
                    if(player.hasEffect(Registration.ROTTING.get()) && player.isAlive()) {
                        // If so, rot the player
                        rotPlayer(player);
                    }
                }
            }
            // If tickCount is a multiple of TICKS_PER_SPEARMASTER_SPEAR
            if(tickCount % TICKS_PER_SPEARMASTER_NEEDLE == 0) {
                // For each player
                for(Player player : level.getServer().getPlayerList().getPlayers()) {
                    // Check if the player has the spearmaster soul in offhand
                    ItemStack offhand = player.getInventory().getItem(Inventory.SLOT_OFFHAND);
                    if(offhand.getItem() == Registration.SPEARMASTER_SOUL.get()) {
                        // If so, give the player a needle
                        player.getInventory().add(new ItemStack(Registration.NEEDLE.get()));
                        // Send a message to the player
                        player.sendSystemMessage(Component.translatable("message.slugcraft.quickfire.needle_gained"));
                    }
                }
            }
        }
    }

    @Override
    public void postTickWorld() {

    }

    @Override
    public void rotPlayer(LivingEntity pLivingEntity) {
        SlugCraft.LOGGER.info("Rotting player "+pLivingEntity.getName().getString()+"...");

        // Get or create rotLevel tag
        if(!pLivingEntity.getPersistentData().contains("rotLevel")) {
            pLivingEntity.getPersistentData().putInt("rotLevel", 0);
        }
        // Increment rotLevel
        int rotLevel = pLivingEntity.getPersistentData().getInt("rotLevel");
        rotLevel++;
        pLivingEntity.getPersistentData().putInt("rotLevel", rotLevel);

        // Reduce player's max health by rotLevel*2
        pLivingEntity.getAttribute(Attributes.MAX_HEALTH).removeModifier(rotLevelUUID);
        pLivingEntity.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(new AttributeModifier(rotLevelUUID, "Rotting", -2*rotLevel, AttributeModifier.Operation.ADDITION));

        // Send ominous message to player
        if(pLivingEntity instanceof Player) {
            Player player = (Player) pLivingEntity;
            player.sendSystemMessage(Component.translatable("message.slugcraft.rotting"));
        }

        // If the player's rot level is 10 or more, apply wither and huge slowness
        if(rotLevel >= 10) {
            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, -1, 1));
            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 3));
            // Send 'you're out of time...' message in dark gray
            // Send ominous message to player
            if(pLivingEntity instanceof Player) {
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
