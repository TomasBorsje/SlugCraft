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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ThrownNeedle extends AbstractArrow {
    private static final ItemStack DEFAULT_NEEDLE_STACK = new ItemStack(Registration.NEEDLE.get());
    private boolean dealtDamage;
    private boolean buffed;

    public ThrownNeedle(EntityType<? extends ThrownNeedle> p_37561_, Level p_37562_) {
        super(p_37561_, p_37562_);
        this.pickup = Pickup.DISALLOWED;
    }

    public ThrownNeedle(LivingEntity livingEntity, Level level) {
        super(Registration.THROWN_NEEDLE.get(), livingEntity, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        super.tick();
    }

    protected ItemStack getPickupItem() {
        return DEFAULT_NEEDLE_STACK.copy();
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 p_37575_, Vec3 p_37576_) {
        return this.dealtDamage ? null : super.findHitEntity(p_37575_, p_37576_);
    }

    protected void onHitEntity(EntityHitResult p_37573_) {
        Entity victim = p_37573_.getEntity();
        float dmg = 16.0F;
        if (victim instanceof LivingEntity $$3) {
            dmg += EnchantmentHelper.getDamageBonus(DEFAULT_NEEDLE_STACK, $$3.getMobType());
        }

        Entity owner = this.getOwner();
        DamageSource $$5 = this.damageSources().trident(this, (Entity)(owner == null ? this : owner));
        this.dealtDamage = true;
        SoundEvent $$6 = SoundEvents.TRIDENT_HIT;
        if (victim.hurt($$5, dmg)) {
            if (victim.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (victim instanceof LivingEntity) {
                LivingEntity $$7 = (LivingEntity)victim;
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects($$7, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)owner, $$7);
                }

                // Grant buffs to the owner
                if(!buffed && owner instanceof Player player && victim instanceof Player target) {
                    this.buffed = true;

                    // Send message to the thrower
                    //player.sendSystemMessage(Component.translatable("item.slugcraft.needle.hit", victim.getName()));

                    // Send message to the target, if they're a player
                    //target.sendSystemMessage(Component.translatable("item.slugcraft.needle.receive_hit", player.getName()));

                    // Slow the target
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20*10, 1));

                    // Clear absoprtion and movement speed if they exist
                    player.removeEffect(MobEffects.ABSORPTION);
                    player.removeEffect(MobEffects.MOVEMENT_SPEED);
                    // Add new absorption and speed to the thrower
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, -1, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20*30, 1));
                    player.getFoodData().setFoodLevel(20);
                }

                this.doPostHurtEffects($$7);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        float $$8 = 1.0F;

        this.playSound($$6, $$8, 1.0F);
    }

    protected boolean tryPickup(Player p_150196_) {
        return super.tryPickup(p_150196_) || this.isNoPhysics() && this.ownedBy(p_150196_) && p_150196_.getInventory().add(this.getPickupItem());
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
        if (this.pickup != Pickup.ALLOWED) {
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
