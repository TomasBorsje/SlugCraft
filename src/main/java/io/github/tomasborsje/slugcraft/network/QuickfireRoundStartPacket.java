package io.github.tomasborsje.slugcraft.network;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.renderers.HardRainRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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

                // Get instance and stop threat music sound
                Minecraft.getInstance().getSoundManager().stop(new ResourceLocation(SlugCraft.MODID, "threat_garbage_wastes"), null);
                // Stop hard rain
                HardRainRenderer.isHardRain = false;
            });
        });
    }
}
