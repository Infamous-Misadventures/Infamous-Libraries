package com.infamousmisadventures.infamouslibraries.followers.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

import static com.infamousmisadventures.infamouslibraries.followers.FollowerLeaderHelper.getLeader;
import static com.infamousmisadventures.infamouslibraries.followers.entity.ai.goal.GoalHelper.shouldAttackEntity;


public class LeaderHurtByTargetGoal extends TargetGoal {
    private final Mob mobEntity;
    private LivingEntity attacker;
    private int timestamp;

    public LeaderHurtByTargetGoal(Mob mobEntity) {
        super(mobEntity, false);
        this.mobEntity = mobEntity;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
//        if (this.mobEntity.isPlayerCreated()) {
        LivingEntity owner = getLeader(this.mobEntity);
        if (owner == null) {
            return false;
        } else {
            this.attacker = owner.getLastHurtByMob();
            int revengeTimer = owner.getLastHurtByMobTimestamp();
            return revengeTimer != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && shouldAttackEntity(this.attacker, owner);
        }
//        } else {
//            return false;
//        }
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity owner = getLeader(this.mobEntity);
        if (owner != null) {
            this.timestamp = owner.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
