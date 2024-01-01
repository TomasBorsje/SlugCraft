package io.github.tomasborsje.slugcraft.quickfire;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IQuickfireCapability {
    boolean getRoundRunning();
    void setRoundRunning(boolean value);
    void startRound(Level level, Player player);
    void tickWorld();
    void postTickWorld();
}
