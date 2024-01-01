package io.github.tomasborsje.slugcraft.quickfire;

import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.Random;

public class QuickfireCapability implements IQuickfireCapability {
    public boolean isRoundRunning = false;
    final Item[] SLUGCAT_SOULS = {Registration.HUNTER_SOUL.get(), Registration.RIVULET_SOUL.get()};
    Random random = new Random();

    /**
     * Executes a text command server-side.
     */
    private void executeCommand(Level level, String command) {
        MinecraftServer server = level.getServer();
        server.getGameRules().getRule(GameRules.RULE_SENDCOMMANDFEEDBACK).set(false, server);
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
    }

    @Override
    public void startRound(Level level, Player startPlayer) {
        // Show round start title to all players
        executeCommand(level, "/title @a title \"Round start!\"");
        // For each player
        level.getServer().getPlayerList().getPlayers().forEach(player -> {
            // Clear inventory
            player.getInventory().clearContent();
            // Give a random Slugcat soul into the offhand
            Item slugcatSoul = SLUGCAT_SOULS[random.nextInt(SLUGCAT_SOULS.length)];
            player.getInventory().setItem(Inventory.SLOT_OFFHAND, new ItemStack(slugcatSoul));
        });
    }

    @Override
    public void tickWorld() {

    }

    @Override
    public void postTickWorld() {

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
