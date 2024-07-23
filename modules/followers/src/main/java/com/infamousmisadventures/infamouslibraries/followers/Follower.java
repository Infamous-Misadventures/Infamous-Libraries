package com.infamousmisadventures.infamouslibraries.followers;

import com.infamousmisadventures.infamouslibraries.general.INBTSerializable;
import com.infamousmisadventures.infamouslibraries.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;


public class Follower implements INBTSerializable<CompoundTag> {

    private UUID leaderUUID;
    private ResourceLocation levelOnLoad;
    private LivingEntity leader;
    private int followerDuration = 0;
    private boolean temporary = false;
    private boolean revertsOnExpiration = false;
    private boolean goalsAdded = false;

    @Nullable
    public LivingEntity getLeader() {
        if (this.leader == null && this.leaderUUID != null && this.levelOnLoad != null) {
            ResourceKey<Level> registrykey1 = ResourceKey.create(Registries.DIMENSION, this.levelOnLoad);
            MinecraftServer server = Services.PLATFORM.getCurrentServer();
            ServerLevel world = server.getLevel(registrykey1);
            if (world != null) {
                Entity entity = world.getEntity(leaderUUID);
                if (entity instanceof LivingEntity) {
                    this.leader = (LivingEntity) entity;
                }
            }
        }
        return this.leader;
    }

    public void setLeader(@Nullable LivingEntity leader) {
        this.leader = leader;
        if (leader != null) {
            this.leaderUUID = leader.getUUID();
            this.levelOnLoad = leader.level().dimension().location();
        } else {
            this.leaderUUID = null;
            this.levelOnLoad = null;
        }
    }

    public void setLeaderUUID(UUID leaderUUID) {
        this.leaderUUID = leaderUUID;
    }

    public void setLevelOnLoad(ResourceLocation levelOnLoad) {
        this.levelOnLoad = levelOnLoad;
    }

    public boolean isFollower() {
        return getLeader() != null;
    }

    public void setFollowerDuration(int followerDuration) {
        this.followerDuration = followerDuration;
    }

    public int getFollowerDuration() {
        return this.followerDuration;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public boolean revertsOnExpiration() {
        return revertsOnExpiration;
    }

    public void setRevertsOnExpiration(boolean revertsOnExpiration) {
        this.revertsOnExpiration = revertsOnExpiration;
    }

    public boolean isGoalsAdded() {
        return goalsAdded;
    }

    public void setGoalsAdded(boolean goalsAdded) {
        this.goalsAdded = goalsAdded;
    }
    
    public void tick(LivingEntity entity){
        if (isFollower()) {
            if (isTemporary()) {
                if (getFollowerDuration() > 0) {
                    setFollowerDuration(getFollowerDuration() - 1);
                } else {
                    if (revertsOnExpiration()) {
                        FollowerLeaderHelper.removeFollower(entity);
                    } else {
                        entity.remove(Entity.RemovalReason.KILLED);
                    }
                }
            }
        }
    }

    public void onDeath(LivingEntity entity){
        if (isFollower()) {
            FollowerLeaderHelper.removeFollower(entity);
        }
    }

    public static final String LEADER_KEY = "leader";
    public static final String LEVEL_KEY = "level";
    public static final String SUMMON_FLAG_KEY = "is_summon";
    public static final String TEMPORARY_FLAG_KEY = "is_temporary";
    public static final String REVERTS_ON_EXPIRATION_FLAG_KEY = "reverts_on_expiration";
    public static final String FOLLOWER_DURATION_KEY = "follower_duration";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (this.getLeader() != null) {
            tag.putUUID(LEADER_KEY, this.leaderUUID);
            ResourceLocation location = this.getLeader().level().dimension().location();
            tag.putString(LEVEL_KEY, location.toString());
        }
        tag.putBoolean(TEMPORARY_FLAG_KEY, this.isTemporary());
        tag.putBoolean(REVERTS_ON_EXPIRATION_FLAG_KEY, this.revertsOnExpiration());
        tag.putInt(FOLLOWER_DURATION_KEY, this.getFollowerDuration());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.hasUUID(LEADER_KEY)) {
            this.setLeaderUUID(tag.getUUID(LEADER_KEY));
        }
        if (tag.contains(LEVEL_KEY)) {
            this.setLevelOnLoad(new ResourceLocation(tag.getString(LEVEL_KEY)));
        }
        if (tag.contains(TEMPORARY_FLAG_KEY)) {
            this.setTemporary(tag.getBoolean(TEMPORARY_FLAG_KEY));
        }
        if (tag.contains(REVERTS_ON_EXPIRATION_FLAG_KEY)) {
            this.setRevertsOnExpiration(tag.getBoolean(REVERTS_ON_EXPIRATION_FLAG_KEY));
        }
        if (tag.contains(FOLLOWER_DURATION_KEY)) {
            this.setFollowerDuration(tag.getInt(FOLLOWER_DURATION_KEY));
        }
    }

    public void onEntityJoin(Mob mob) {
        FollowerLeaderHelper.addFollowerGoals(mob);
    }
}
