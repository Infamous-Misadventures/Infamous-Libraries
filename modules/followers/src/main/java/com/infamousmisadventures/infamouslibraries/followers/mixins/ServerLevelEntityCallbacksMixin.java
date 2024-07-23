package com.infamousmisadventures.infamouslibraries.followers.mixins;

import com.infamousmisadventures.infamouslibraries.followers.IFollowerDataHolder;
import com.infamousmisadventures.infamouslibraries.followers.ILeaderDataHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.EntityCallbacks.class)
public class ServerLevelEntityCallbacksMixin {


    @Inject(method = "Lnet/minecraft/server/level/ServerLevel$EntityCallbacks;onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void onTrackingStart(Entity entity, CallbackInfo ci) {
        if (entity instanceof Mob mob){
            ((IFollowerDataHolder) mob).getOrCreateFollowerData().onEntityJoin(mob);
        }
        if(entity instanceof LivingEntity livingEntity){
            ((ILeaderDataHolder) livingEntity).getOrCreateLeaderData().onEntityJoin(livingEntity);
        }
    }
}
