package io.github.tomasborsje.slugcraft.entities;

import io.github.tomasborsje.slugcraft.core.Registration;
import io.github.tomasborsje.slugcraft.network.DoClientsideParticleEffectPacket;
import io.github.tomasborsje.slugcraft.network.PacketHandler;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownSingularityGrenade extends ThrowableItemProjectile {
    private final static float HURT_RADIUS = 5.5f;
    private final static float PULL_SPEED = 2.5f;
    private final static float PULL_RADIUS = 32f;
    private final static int LIFETIME = 80;
    private int ticksExploding = 0;
    private boolean exploded;
    public ThrownSingularityGrenade(EntityType<? extends ThrownSingularityGrenade> type, Level level) {
        super(type, level);
    }

    public ThrownSingularityGrenade(Level level, LivingEntity entity) {
        super(Registration.THROWN_SINGULARITY_GRENADE.get(), entity, level);
    }

    public ThrownSingularityGrenade(Level level, double x, double y, double z) {
        super(Registration.THROWN_SINGULARITY_GRENADE.get(), x, y, z, level);
    }
    private ParticleOptions getParticle() {
        ItemStack stack = this.getItemRaw();
        return stack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, stack);
    }

    public void handleEntityEvent(byte p_37402_) {
        if (p_37402_ == 3) {
            ParticleOptions particle = this.getParticle();
            for(int $$2 = 0; $$2 < 8; ++$$2) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    protected void onHitEntity(EntityHitResult entityHit) {
        super.onHitEntity(entityHit);

        Entity target = entityHit.getEntity();
        target.hurt(this.damageSources().thrown(this, this.getOwner()), 1);
    }

    @Override
    public void tick() {
        super.tick();
        // Return clientside
        if (this.level().isClientSide) {
            return;
        }

        if(exploded) {
            ticksExploding++;
            this.setDeltaMovement(Vec3.ZERO);

            // Every 3 ticks, get all players within 32 blocks and pull them in
            if (ticksExploding % 2 == 0) {
                for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(PULL_RADIUS))) {
                    // Don't affect self
                    if (entity == this) { continue; }

                    // Create Vector3 that points from the player to the grenade
                    Vec3 vector = this.position().subtract(entity.position());

                    // If the entity is less than 3 squared blocks away, pull them in slower
                    if (vector.lengthSqr() < 3*3) {
                        vector = vector.normalize().scale(0.25*PULL_SPEED);
                    }
                    else {
                        vector = vector.normalize().scale(PULL_SPEED);
                    }

                    entity.resetFallDistance(); // Reset fall distance so they don't take fall damage

                    // If player, send packet to move them
                    if (entity instanceof ServerPlayer player) {
                        // Set player's deltaMovement to the vector
                        player.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), vector));
                    }
                    // Else if living entity, move them
                    else if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.setDeltaMovement(vector);
                    }
                }
            }
            // Every 5 ticks, do the particle effect
            if (ticksExploding % 5 == 0) {
                PacketHandler.sendToAll(new DoClientsideParticleEffectPacket(DoClientsideParticleEffectPacket.ParticleEffect.SINGULARITY_GRENADE, this.getX(), this.getY(), this.getZ()));
            }

            // Every second do a small explosion
            if (ticksExploding % 20 == 0) {
                this.level().explode(null, null, null , this.getX(), this.getY(), this.getZ(), 0.0F, false, Level.ExplosionInteraction.BLOCK);
            }

            // If lifetime is up, explode and discard
            if(ticksExploding >= LIFETIME) {
                this.level().explode(null, null, null , this.getX(), this.getY(), this.getZ(), 1.5F, false, Level.ExplosionInteraction.BLOCK);
                this.discard();
            }
        }
    }

    void startExploding() {
        // If clientside
        if (this.level().isClientSide) {
            return;
        }
        if(!exploded) {
            exploded = true;

            // Send particle effect
            PacketHandler.sendToAll(new DoClientsideParticleEffectPacket(DoClientsideParticleEffectPacket.ParticleEffect.SINGULARITY_GRENADE, this.getX(), this.getY(), this.getZ()));

            // Make a small explosion
            this.level().explode(null, null, null , this.getX(), this.getY(), this.getZ(), 3F, false, Level.ExplosionInteraction.BLOCK);

            // Play explosion sound
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, net.minecraft.sounds.SoundSource.NEUTRAL, 0.85F, 1.0F);
            // Spawn a lightning bolt here
            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, this.level());
            lightningBolt.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(lightningBolt);

            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    protected void onHit(HitResult hit) {
        super.onHit(hit);

        startExploding();
    }


    @Override
    protected Item getDefaultItem() {
        return Registration.SINGULARITY_GRENADE.get();
    }
}
