package com.infamousmisadventures.infamouslibraries.summons.summoner;

import com.infamousmisadventures.infamouslibraries.followers.Leader;
import com.infamousmisadventures.infamouslibraries.general.INBTSerializable;
import com.infamousmisadventures.infamouslibraries.summons.summon.config.SummonConfigRegistry;
import com.infamousmisadventures.infamouslibraries.summons.SummonHelper;
import com.infamousmisadventures.infamouslibraries.summons.summon.ISummonDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class Summoner implements INBTSerializable<CompoundTag> {

    private Leader leader;

    public Summoner(Leader leader) {
        this.leader = leader;
    }

    public Leader getLeader() {
        return leader;
    }

    public List<Entity> getSummonedMobs() {
        return leader.getFollowers().stream()
                .filter(entity -> entity instanceof ISummonDataHolder summonDataHolder && summonDataHolder.getOrCreateSummonData().isSummon())
                .toList();
    }

    public int getSummonedMobsCost() {
        return leader.getFollowers().stream()
                .filter(entity -> entity instanceof ISummonDataHolder summonDataHolder && summonDataHolder.getOrCreateSummonData().isSummon())
                .map(entity -> ((ISummonDataHolder) entity).getOrCreateSummonData())
                .map(summon -> summon.getConfig().getCost()).reduce(0, Integer::sum);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
    }

    public void onEntityJoin(LivingEntity livingEntity) {
        List<Entity> minions = leader.getFollowers();
        for (Entity entity : minions) {
            if (entity instanceof Mob mob) {
                SummonHelper.addSummonGoals(mob);
            }
        }
    }
}