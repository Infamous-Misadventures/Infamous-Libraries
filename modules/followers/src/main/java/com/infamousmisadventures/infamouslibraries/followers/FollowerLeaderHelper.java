package com.infamousmisadventures.infamouslibraries.followers;

import com.infamousmisadventures.infamouslibraries.ILConstants;
import com.infamousmisadventures.infamouslibraries.followers.entity.ai.goal.FollowerFollowLeaderGoal;
import com.infamousmisadventures.infamouslibraries.followers.entity.ai.goal.LeaderHurtByTargetGoal;
import com.infamousmisadventures.infamouslibraries.followers.entity.ai.goal.LeaderHurtTargetGoal;
import com.infamousmisadventures.infamouslibraries.followers.mixins.MobInvoker;
import com.infamousmisadventures.infamouslibraries.general.mixins.MobAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class FollowerLeaderHelper {

    public static Leader getLeaderCapability(LivingEntity entity) {
        return ((ILeaderDataHolder) entity).getOrCreateLeaderData();
    }

    public static Follower getFollowerCapability(LivingEntity entity) {
        return ((IFollowerDataHolder) entity).getOrCreateFollowerData();
    }

    @Nullable
    public static LivingEntity getOwnerForHorse(AbstractHorse horseEntity) {
        try {
            if (horseEntity.getOwnerUUID() != null) {
                UUID ownerUniqueId = horseEntity.getOwnerUUID();
                return ownerUniqueId == null ? null : horseEntity.level().getPlayerByUUID(ownerUniqueId);
            } else return null;
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public static boolean isFollowerRelated(LivingEntity origin, LivingEntity target) {
        Follower follower = ((IFollowerDataHolder) origin).getOrCreateFollowerData();
        if(follower.isFollower()) {
            return isFollowerOf(origin, target) || isFollowerOf(target, follower.getLeader());
        }
        Leader leader = ((ILeaderDataHolder) origin).getOrCreateLeaderData();
        return leader.isLeader() && leader.isLeaderOf(target);
    }

    public static boolean isFollower(LivingEntity target) {
        return getFollowerCapability(target).getLeader() != null;
    }

    public static boolean isFollowerOf(LivingEntity targetFollower, LivingEntity targetLeader) {
        Follower followerData = getFollowerCapability(targetFollower);
        return followerData.getLeader() != null
                && followerData.getLeader() == targetLeader;
    }

    @Nullable
    public static LivingEntity getLeader(LivingEntity minionMob) {
        Follower minion = getFollowerCapability(minionMob);
        return minion.getLeader();
    }

    public static boolean isSuitableNavigationForFollowLeader(Mob mobEntity) {
        return mobEntity.getNavigation() instanceof GroundPathNavigation || mobEntity.getNavigation() instanceof FlyingPathNavigation;
    }

    private void makeFollowerOf(LivingEntity livingEntity, LivingEntity nearbyEntity) {
        if (nearbyEntity instanceof Monster) {
            Monster mobEntity = (Monster) nearbyEntity;
            Leader leaderCapability = FollowerLeaderHelper.getLeaderCapability(livingEntity);
            Follower minionCapability = FollowerLeaderHelper.getFollowerCapability(nearbyEntity);
            leaderCapability.addFollower(mobEntity);
            minionCapability.setLeader(livingEntity);
            minionCapability.setGoalsAdded(false);
            ((Monster) nearbyEntity).setTarget(null);
            addFollowerGoals(mobEntity);
        }
    }

    private void makeTemporaryFollowerOf(LivingEntity livingEntity, LivingEntity nearbyEntity, int followerDuration, boolean revertsOnExpiration) {
        if (nearbyEntity instanceof Mob mob) {
            Leader leaderCapability = FollowerLeaderHelper.getLeaderCapability(livingEntity);
            Follower minionCapability = FollowerLeaderHelper.getFollowerCapability(nearbyEntity);
            leaderCapability.addFollower(mob);
            minionCapability.setLeader(livingEntity);
            minionCapability.setTemporary(true);
            minionCapability.setRevertsOnExpiration(revertsOnExpiration);
            minionCapability.setFollowerDuration(followerDuration);
            minionCapability.setGoalsAdded(false);
            mob.setTarget(null);
            addFollowerGoals(mob);
        }
    }

    public static void addFollowerGoals(Mob mobEntity) {
        Follower follower = getFollowerCapability(mobEntity);
        if (follower.isGoalsAdded()) return;
        if (follower.isFollower()) {
            if(!isSuitableNavigationForFollowLeader(mobEntity)){
                ILConstants.LOGGER.error("Unsupported mob type for FollowerFollowLeaderGoal: {}", mobEntity.getType());
                return;
            }
            ((MobAccessor) mobEntity).getGoalSelector().addGoal(2, new FollowerFollowLeaderGoal(mobEntity, 1.5D, 24.0F, 3.0F, false));
            clearGoals(((MobAccessor) mobEntity).getTargetSelector());
            ((MobAccessor) mobEntity).getTargetSelector().addGoal(1, new LeaderHurtByTargetGoal(mobEntity));
            ((MobAccessor) mobEntity).getTargetSelector().addGoal(2, new LeaderHurtTargetGoal(mobEntity));
            /*((mobAccessor) mobEntity).getTargetSelector().addGoal(3, new NearestAttackableTargetGoal<>(mobEntity, LivingEntity.class, 5, false, false,
                    (entityIterator) -> AbilityHelper.isDefaultEnemy(entityIterator) && canPetAttackEntity(mobEntity, entityIterator)));*/

            Leader leader = ((ILeaderDataHolder) follower.getLeader()).getOrCreateLeaderData();
            leader.addFollower(mobEntity);
            //SummonHelper.addSummonGoals(mobEntity);
            follower.setGoalsAdded(true);
        }
    }

    public static void removeFollower(LivingEntity entityLiving) {
        Follower cap = getFollowerCapability(entityLiving);
        LivingEntity leader = cap.getLeader();
        Leader leaderCapability = getLeaderCapability(leader);
        leaderCapability.removeFollower(entityLiving);
        cap.setLeader(null);
        if (entityLiving instanceof Mob mobEntity && entityLiving.isAlive()) {
            clearGoals(((MobAccessor) mobEntity).getGoalSelector());
            clearGoals(((MobAccessor) mobEntity).getTargetSelector());
            cap.setGoalsAdded(false);
            ((MobInvoker) entityLiving).invokeRegisterGoals();
        }
    }

    private static void clearGoals(GoalSelector goalSelector) {
        ArrayList<WrappedGoal> wrappedGoals = new ArrayList<>(goalSelector.getAvailableGoals());
        wrappedGoals.forEach(prioritizedGoal -> goalSelector.removeGoal(prioritizedGoal.getGoal()));
    }
}
