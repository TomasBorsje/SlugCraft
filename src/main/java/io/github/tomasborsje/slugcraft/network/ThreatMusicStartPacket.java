package io.github.tomasborsje.slugcraft.network;

import io.github.tomasborsje.slugcraft.SlugCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ThreatMusicStartPacket {
    private final ResourceLocation soundName;
    public ThreatMusicStartPacket(ResourceLocation soundName) {
         this.soundName = soundName;
    }

    public ThreatMusicStartPacket(FriendlyByteBuf buffer) {
        this.soundName = buffer.readResourceLocation();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(soundName);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                SlugCraft.LOGGER.info("CLIENT: Received threat music start packet");
                SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(soundName);
                // Get instance and stop threat music sound
                if (Minecraft.getInstance().player != null && sound != null) {
                    Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(sound, SoundSource.RECORDS, 1.0F, 1.0F, Minecraft.getInstance().player, 0));
                }
            });
        });
    }
}
