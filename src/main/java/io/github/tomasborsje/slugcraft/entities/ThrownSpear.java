package io.github.tomasborsje.slugcraft.entities;

import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

public class ThrownSpear extends AbstractArrow {
    private static final ItemStack DEFAULT_SPEAR_STACK = new ItemStack(Registration.SPEAR.get());
    public static final float SPEAR_DAMAGE = 8.0F;
    private boolean dealtDamage;
    private ItemStack thrownStack = DEFAULT_SPEAR_STACK;

    public ThrownSpear(EntityType<? extends ThrownSpear> p_37561_, Level p_37562_) {
        super(p_37561_, p_37562_);
    }

    public ThrownSpear(Level p_37569_, LivingEntity p_37570_, ItemStack stack) {
        super(Registration.THROWN_SPEAR.get(), p_37570_, p_37569_);
        this.thrownStack = stack.copy();
    }

    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        super.tick();
    }

    protected ItemStack getPickupItem() {
        return this.thrownStack.copy();
    }


    @Nullable
    protected EntityHitResult findHitEntity(Vec3 p_37575_, Vec3 p_37576_) {
        return this.dealtDamage ? null : super.findHitEntity(p_37575_, p_37576_);
    }

    protected void onHitEntity(EntityHitResult p_37573_) {
        Entity $$1 = p_37573_.getEntity();

        Entity $$4 = this.getOwner();
        DamageSource $$5 = this.damageSources().trident(this, (Entity)($$4 == null ? this : $$4));
        this.dealtDamage = true;
        SoundEvent $$6 = SoundEvents.TRIDENT_HIT;
        if ($$1.hurt($$5, SPEAR_DAMAGE)) {
            if ($$1.getType() == EntityType.ENDERMAN) {
                return;
            }

            if ($$1 instanceof LivingEntity) {
                LivingEntity $$7 = (LivingEntity)$$1;
                if ($$4 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects($$7, $$4);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)$$4, $$7);
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
