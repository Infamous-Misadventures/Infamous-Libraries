package com.infamousmisadventures.infamouslibraries.followers.mixins;

import com.infamousmisadventures.infamouslibraries.followers.FollowerLeaderHelper;
import com.infamousmisadventures.infamouslibraries.followers.IFollowerDataHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mob.class)
public abstract class MobMixin extends LivingEntity{

    @Shadow
    private LivingEntity target;

    protected MobMixin(EntityType<? extends LivingEntity> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Inject(method = "setTarget", at = @At("TAIL"))
    public void il_followers$setTarget(LivingEntity target, CallbackInfo ci) {
        if (target != null) {
            if (FollowerLeaderHelper.isFollowerRelated(this, target)) {
                this.target = null;
                if (this instanceof NeutralMob) {
                    ((NeutralMob) this).setPersistentAngerTarget(null);
                    ((NeutralMob) this).setRemainingPersistentAngerTime(0);
                }
            }
        }
    }
}
