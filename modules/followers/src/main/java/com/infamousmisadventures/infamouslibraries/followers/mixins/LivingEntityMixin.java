package com.infamousmisadventures.infamouslibraries.followers.mixins;

import com.infamousmisadventures.infamouslibraries.followers.*;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILeaderDataHolder, IFollowerDataHolder {

    @Shadow public abstract boolean canAttack(LivingEntity pLivingentity, TargetingConditions pCondition);

    protected LivingEntityMixin(EntityType<? extends LivingEntity> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Unique
    private Leader il_followers$leader = null;

    @Unique
    private Follower il_followers$follower = null;

    public Leader getOrCreateLeaderData() {
        if (il_followers$leader == null) {
            il_followers$leader = new Leader();
        }
        return il_followers$leader;
    }

    public Follower getOrCreateFollowerData() {
        if (il_followers$follower == null) {
            il_followers$follower = new Follower();
        }
        return il_followers$follower;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        nbt.put("LeaderData", this.getOrCreateLeaderData().serializeNBT());
        nbt.put("FollowerData", this.getOrCreateFollowerData().serializeNBT());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        getOrCreateLeaderData().deserializeNBT(nbt.getCompound("LeaderData"));
        getOrCreateFollowerData().deserializeNBT(nbt.getCompound("FollowerData"));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo callbackInfo) {
        getOrCreateFollowerData().tick(il_followers$getAsLivingEntity());
        getOrCreateLeaderData().tick(this.tickCount);
    }

    @Unique
    private @NotNull LivingEntity il_followers$getAsLivingEntity() {
        return (LivingEntity) (Object) this;
    }

    @Inject(method = "die", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void die(DamageSource damageSource, CallbackInfo callbackInfo) {
        getOrCreateFollowerData().onDeath(il_followers$getAsLivingEntity());
    }

    @ModifyReturnValue(method = "Lnet/minecraft/world/entity/LivingEntity;canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"))
    public boolean il_followers$canAttack(boolean returnValue, LivingEntity $$0) {
        return returnValue && il_followers$canAttackSub($$0);
    }

    @Unique
    private boolean il_followers$canAttackSub(LivingEntity target){
        if (!this.level().isClientSide() && target != null) {
            FollowerLeaderHelper.isFollowerRelated((LivingEntity) (Object) this, target);
        }
        return true;
    }

}
