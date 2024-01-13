package io.github.tomasborsje.slugcraft.network;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import io.github.tomasborsje.slugcraft.sound.ThreatMusicHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class StopThreatMusicPacket {
    public StopThreatMusicPacket() { }
    public StopThreatMusicPacket(FriendlyByteBuf buffer) { }
    public void encode(FriendlyByteBuf friendlyByteBuf) { }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            SlugCraft.LOGGER.info("CLIENT: Stopping threat music");

            // Play threat music
            ThreatMusicHandler.stopThreatMusic();
        }));
    }
}
