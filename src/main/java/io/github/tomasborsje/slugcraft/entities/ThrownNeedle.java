package io.github.tomasborsje.slugcraft.entities;

import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ThrownNeedle extends AbstractArrow {
    private static final ItemStack DEFAULT_NEEDLE_STACK = new ItemStack(Registration.NEEDLE.get());
    private boolean dealtDamage;

    public ThrownNeedle(EntityType<? extends ThrownNeedle> type, Level level) {
        super(type, level, DEFAULT_NEEDLE_STACK);
    }
    public ThrownNeedle(EntityType<? extends ThrownNeedle> type, LivingEntity owner, Level level) {
        super(type, owner, level, DEFAULT_NEEDLE_STACK);
    }

    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        super.tick();
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 p_37575_, Vec3 p_37576_) {
        return this.dealtDamage ? null : super.findHitEntity(p_37575_, p_37576_);
    }

    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 8.0F;
        if (entity instanceof LivingEntity livingentity) {
            f += EnchantmentHelper.getDamageBonus(this.getPickupItemStackOrigin(), livingentity.getMobType());
        }

        Entity owner = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, (Entity)(owner == null ? this : owner));
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if(owner instanceof Player player) {
                // Send message to the thrower
                player.sendSystemMessage(Component.translatable("item.slugcraft.needle.hit", entity.getName()));
                // Send message to the target, if they're a player
                if(entity instanceof Player target) {
                    target.sendSystemMessage(Component.translatable("item.slugcraft.needle.receive_hit"));
                    // Slow the target
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20*5, 1));
                }
                // Add absorption and speed to the thrower
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, -1, 2));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20*30, 1));
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity)entity;
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity1, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)owner, livingentity1);
                }

                this.doPostHurtEffects(livingentity1);
            }
        } else if (entity.getType().is(EntityTypeTags.DEFLECTS_TRIDENTS)) {
            this.deflect();
            return;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;

        this.playSound(soundevent, f1, 1.0F);
    }

    protected boolean tryPickup(Player p_150196_) {
        return false;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    public void playerTouch(Player p_37580_) {
        if (this.ownedBy(p_37580_) || this.getOwner() == null) {
            super.playerTouch(p_37580_);
        }

    }

    public void readAdditionalSaveData(CompoundTag p_37578_) {
        super.readAdditionalSaveData(p_37578_);
        this.dealtDamage = p_37578_.getBoolean("DealtDamage");
    }

    public void addAdditionalSaveData(CompoundTag p_37582_) {
        super.addAdditionalSaveData(p_37582_);
        p_37582_.putBoolean("DealtDamage", this.dealtDamage);
    }

    public void tickDespawn() {
        if (this.pickup != AbstractArrow.Pickup.ALLOWED) {
            super.tickDespawn();
        }
    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    public boolean shouldRender(double p_37588_, double p_37589_, double p_37590_) {
        return true;
    }
}
