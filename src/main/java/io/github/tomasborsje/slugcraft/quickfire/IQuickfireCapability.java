package io.github.tomasborsje.slugcraft.quickfire;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IQuickfireCapability {
    boolean getRoundRunning();
    void setRoundRunning(boolean value);
    void startRound(ServerLevel level);
    void startPreRound(ServerLevel level, ServerPlayer player);
    void endRound(ServerLevel level, Player winner);
    void tickWorld(ServerLevel level);
    void postTickWorld();

    void rotPlayer(LivingEntity pLivingEntity);
}
