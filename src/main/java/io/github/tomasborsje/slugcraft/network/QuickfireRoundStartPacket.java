package io.github.tomasborsje.slugcraft.network;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.renderers.HardRainRenderer;
import io.github.tomasborsje.slugcraft.sound.ThreatMusicHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class QuickfireRoundStartPacket {
    public QuickfireRoundStartPacket() {

    }

    public QuickfireRoundStartPacket(FriendlyByteBuf buffer) {
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                SlugCraft.LOGGER.info("CLIENT: Received quickfire round start packet");

                // Stop threat music sound
                ThreatMusicHandler.stopThreatMusic();
                // Stop hard rain
                HardRainRenderer.isHardRain = false;
            });
        });
    }
}
