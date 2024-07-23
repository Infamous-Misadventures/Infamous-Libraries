package com.infamousmisadventures.infamouslibraries.followers.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;

import static com.infamousmisadventures.infamouslibraries.followers.FollowerLeaderHelper.isFollower;
import static com.infamousmisadventures.infamouslibraries.followers.FollowerLeaderHelper.isFollowerOf;

public class GoalHelper {

    public static boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof Creeper) && !(target instanceof Ghast)) {
            if (target instanceof Wolf) {
                Wolf wolfentity = (Wolf) target;
                if (wolfentity.isTame() && wolfentity.getOwner() == owner) {
                    return false;
                }
            } else if (isFollower(target)) {
                return !isFollowerOf(target, owner);
            }

            if (target instanceof Player && owner instanceof Player && !((Player) owner).canHarmPlayer((Player) target)) {
                return false;
            } else if (target instanceof AbstractHorse && ((AbstractHorse) target).isTamed()) {
                return false;
            } else {
                return !(target instanceof Cat) || !((Cat) target).isTame();
            }
        } else {
            return false;
        }
    }
}
