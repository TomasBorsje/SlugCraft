package io.github.tomasborsje.slugcraft.entities;

import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.core.Registration;
import io.github.tomasborsje.slugcraft.network.DoClientsideParticleEffectPacket;
import io.github.tomasborsje.slugcraft.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownSporePuff extends ThrowableItemProjectile {
    private final static float HURT_RADIUS = 5.5f;
    public ThrownSporePuff(EntityType<? extends ThrownSporePuff> type, Level level) {
        super(type, level);
    }

    public ThrownSporePuff(Level level, LivingEntity entity) {
        super(Registration.THROWN_SPORE_PUFF.get(), entity, level);
    }

    public ThrownSporePuff(Level level, double x, double y, double z) {
        super(Registration.THROWN_SPORE_PUFF.get(), x, y, z, level);
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

    void explode() {
        // If clientside
        if (this.level().isClientSide) {
            return;
        }

        // Apply debuffs to entities in range
        AABB hurtBox = new AABB(this.getX() - HURT_RADIUS, this.getY() - HURT_RADIUS, this.getZ() - HURT_RADIUS, this.getX() + HURT_RADIUS, this.getY() + HURT_RADIUS, this.getZ() + HURT_RADIUS);
        for (Entity entity : this.level().getEntities(this, hurtBox)) {
            if (entity instanceof LivingEntity) {
                // Blindness for 5 seconds (prevents sprinting so kinda OP)
                ((LivingEntity) entity).addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.BLINDNESS, 100, 0));
                // Poison level 1 for 5 seconds
                ((LivingEntity) entity).addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, 100, 0));
            }
        }
        // Send particle effect
        PacketHandler.sendToAll(new DoClientsideParticleEffectPacket(DoClientsideParticleEffectPacket.ParticleEffect.SPORE_PUFF, this.getX(), this.getY(), this.getZ()));

        // Play explosion sound
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, net.minecraft.sounds.SoundSource.NEUTRAL, 0.85F, 1.0F);
    }

    protected void onHit(HitResult hit) {
        super.onHit(hit);

        explode();

        if (!this.level().isClientSide) {
            this.discard();
        }
    }


    @Override
    protected Item getDefaultItem() {
        return Registration.SPORE_PUFF.get();
    }
}
