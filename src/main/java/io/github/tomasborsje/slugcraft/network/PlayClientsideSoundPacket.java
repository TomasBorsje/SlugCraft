package io.github.tomasborsje.slugcraft.network;

import io.github.tomasborsje.slugcraft.SlugCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class PlayClientsideSoundPacket {
    private final ResourceLocation soundName;
    public PlayClientsideSoundPacket(ResourceLocation soundName) {
         this.soundName = soundName;
    }

    public PlayClientsideSoundPacket(FriendlyByteBuf buffer) {
        this.soundName = buffer.readResourceLocation();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(soundName);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            SlugCraft.LOGGER.info("CLIENT: Playing clientside sound with resource location " + soundName);

            // Play sound
            float volume = 1.0f;
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(soundName);
            if (Minecraft.getInstance().player != null && sound != null) {
                SoundInstance soundInstance = new EntityBoundSoundInstance(sound, SoundSource.RECORDS, volume, 1.0f, Minecraft.getInstance().player, 0);
                Minecraft.getInstance().getSoundManager().play(soundInstance);
            }
        }));
    }
}
