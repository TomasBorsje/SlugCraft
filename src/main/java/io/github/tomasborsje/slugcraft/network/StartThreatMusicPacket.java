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

public class StartThreatMusicPacket {
    private final ResourceLocation soundName;
    private final static Random random = new Random();
    public StartThreatMusicPacket(ResourceLocation soundName) {
        this.soundName = soundName;
    }
    public StartThreatMusicPacket(FriendlyByteBuf buffer) {
        this.soundName = buffer.readResourceLocation();
    }
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(soundName);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            SlugCraft.LOGGER.info("CLIENT: Playing threat sound with resource location " + soundName);

            // Play threat music
            ThreatMusicHandler.playThreatMusic(soundName);
        }));
    }
    public static ResourceLocation randomThreat() {
        return Registration.THREAT_MUSICS[random.nextInt(Registration.THREAT_MUSICS.length)].get().getLocation();
    }
}
