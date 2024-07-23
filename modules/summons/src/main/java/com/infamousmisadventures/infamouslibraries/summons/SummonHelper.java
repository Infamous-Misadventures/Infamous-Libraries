package com.infamousmisadventures.infamouslibraries.summons;

import com.infamousmisadventures.infamouslibraries.followers.Follower;
import com.infamousmisadventures.infamouslibraries.followers.Leader;
import com.infamousmisadventures.infamouslibraries.general.mixins.MobAccessor;
import com.infamousmisadventures.infamouslibraries.summons.summon.ISummonDataHolder;
import com.infamousmisadventures.infamouslibraries.summons.summon.Summon;
import com.infamousmisadventures.infamouslibraries.summons.summon.config.SummonConfig;
import com.infamousmisadventures.infamouslibraries.summons.summoner.ISummonerDataHolder;
import com.infamousmisadventures.infamouslibraries.summons.summoner.Summoner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import static com.infamousmisadventures.infamouslibraries.followers.FollowerLeaderHelper.getFollowerCapability;
import static com.infamousmisadventures.infamouslibraries.followers.FollowerLeaderHelper.getLeaderCapability;
import static com.infamousmisadventures.infamouslibraries.summons.registry.ILSummonsAttributes.SUMMON_CAP;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;

public class SummonHelper {

    public static boolean canSummonMob(LivingEntity leader) {
        Summoner summoner = ((ISummonerDataHolder) leader).getOrCreateSummonerData();
        AttributeInstance summonCostLimitAttribute = leader.getAttribute(SUMMON_CAP.get());
        if (summonCostLimitAttribute == null) return false;
        return summoner.getSummonedMobsCost() < summonCostLimitAttribute.getValue();
    }

    public static Entity summonEntity(LivingEntity leader, BlockPos position, EntityType<?> entityType, SummonConfig summonConfig) {
        Entity entity = entityType.create(leader.level());
        if (entity != null && entity instanceof LivingEntity livingEntity) {
            if (addSummonedMob(leader, summonConfig, entity)) {
                Follower follower = getFollowerCapability(livingEntity);
                follower.setLeader(leader);
                Summon summon = ((ISummonDataHolder) entity).getOrCreateSummonData();
                summon.setSummon(true);
                createSummon(leader, entity, position);
                return entity;
            } else {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        return null;
    }

    private static boolean addSummonedMob(LivingEntity master, SummonConfig summonConfig, Entity entity) {
        Leader leaderCapability = getLeaderCapability(master);
        if (canSummonMob(master, summonConfig)) {
            return leaderCapability.addFollower(entity);
        }
        return false;
    }

    private static void createSummon(LivingEntity master, Entity entity, BlockPos position) {
        entity.moveTo((double) position.getX() + 0.5D, (double) position.getY() + 0.05D, (double) position.getZ() + 0.5D, 0.0F, 0.0F);
        master.level().addFreshEntity(entity);
    }

    private static boolean canSummonMob(LivingEntity leader, SummonConfig summonConfig) {
        AttributeInstance summonCapAttribute = leader.getAttribute(SUMMON_CAP.get());
        if (summonCapAttribute == null) return false;
        Summoner summonCap = ((ISummonerDataHolder) leader).getOrCreateSummonerData();
        return summonCap.getSummonedMobsCost() + summonConfig.getCost() <= summonCapAttribute.getValue();
    }

    public static void addSummonGoals(Mob mobEntity) {
        Summon summon = ((ISummonDataHolder) mobEntity).getOrCreateSummonData();
        if (summon.isSummon()) {
            if (summon.getConfig().shouldAddAttackGoal()) {
                addSummonAttackGoal(mobEntity);
            }
        }
    }

    private static void addSummonAttackGoal(Mob mobEntity) {
        if(mobEntity instanceof PathfinderMob pathfinderMob) {
            AttributeInstance attribute = mobEntity.getAttribute(ATTACK_DAMAGE);
            if (attribute == null) return;
            if (attribute.getValue() == 0) {
                attribute.addTransientModifier(new AttributeModifier("Summon Attack Damage", 1, AttributeModifier.Operation.ADDITION));
            }
            ((MobAccessor) mobEntity).getGoalSelector().addGoal(1, new MeleeAttackGoal(pathfinderMob, 1.0D, true));
        }
    }

}
