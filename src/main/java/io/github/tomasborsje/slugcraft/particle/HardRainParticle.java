package io.github.tomasborsje.slugcraft.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HardRainParticle extends TextureSheetParticle {

    protected HardRainParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, double dx, double dy, double dz) {
        super(level, x, y, z, dx, dy, dz);

        this.friction = 1;
        this.gravity = 2.0f;
        this.lifetime = 60;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.setSpriteFromAge(sprites);
        this.quadSize *= 0.85f;
        this.rCol = 1;
        this.gCol = 1;
        this.bCol = 1;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new HardRainParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
