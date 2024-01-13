package io.github.tomasborsje.slugcraft.network;

import io.github.tomasborsje.slugcraft.SlugCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;
import java.util.function.Supplier;

public class DoClientsideParticleEffectPacket {
    private final static double SMOKE_RADIUS = 1.5f;
    private final static int SMOKE_COUNT = 150;
    private final Random random = new Random();
    private final ParticleEffect effect;
    private final double x;
    private final double y;
    private final double z;

    public enum ParticleEffect {
        SPORE_PUFF
    }
    public DoClientsideParticleEffectPacket(ParticleEffect effect, double x, double y, double z) {
        this.effect = effect;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public DoClientsideParticleEffectPacket(FriendlyByteBuf buffer) {
        this.effect = ParticleEffect.values()[buffer.readInt()];
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(effect.ordinal());
        friendlyByteBuf.writeDouble(x);
        friendlyByteBuf.writeDouble(y);
        friendlyByteBuf.writeDouble(z);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            SlugCraft.LOGGER.info("CLIENT: Spawning particle effect " + effect + " at " + x + ", " + y + ", " + z);
            switch (effect) {
                case SPORE_PUFF:
                    sporePuff();
                    break;
            }
        }));
    }

    void sporePuff() {
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        // Spawn particles
        for(int i = 0; i < SMOKE_COUNT; i++) {
            // Get random offset positions
            double offsetX = this.random.nextGaussian() * SMOKE_RADIUS;
            double offsetY = Math.abs(this.random.nextGaussian() * SMOKE_RADIUS);
            double offsetZ = this.random.nextGaussian() * SMOKE_RADIUS;

            // Spawn particles
            try {
                particleEngine.createParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x + offsetX, y + offsetY, z + offsetZ, 0, 0, 0);
            } catch (Exception e) {
                SlugCraft.LOGGER.error("Failed to spawn spore puff particle", e);
            }
        }
    }
}
