package com.infamousmisadventures.infamouslibraries.followers;

import com.infamousmisadventures.infamouslibraries.general.INBTSerializable;
import com.infamousmisadventures.infamouslibraries.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.stream.Collectors;

public class Leader implements INBTSerializable<CompoundTag> {

    private ResourceLocation levelOnLoad;
    private Set<Entity> followers;
    private final List<UUID> followersUUID = new ArrayList<>();

    public boolean isLeaderOf(Entity entity) {
        return this.getFollowers().contains(entity);
    }

    public boolean isLeader() {
        return !this.getFollowers().isEmpty();
    }

    public void setLevelOnLoad(ResourceLocation levelOnLoad) {
        this.levelOnLoad = levelOnLoad;
    }

    public boolean addFollower(Entity entity) {
        followers = initEntities(this.followers, this.followersUUID);
        return followers.add(entity);
    }

    public List<Entity> getFollowers() {
        followers = initEntities(this.followers, this.followersUUID);
        return new ArrayList<>(this.followers);
    }

    public void setFollowers(List<Entity> followers) {
        this.followers = new HashSet<>(followers);
    }

    private Set<Entity> initEntities(Set<Entity> entities, List<UUID> entityUUIDs) {
        if (entities != null) return entities;
        if (entityUUIDs != null && this.levelOnLoad != null) {
            if (entityUUIDs.isEmpty()) return new HashSet<>();
            ResourceKey<Level> registrykey1 = ResourceKey.create(Registries.DIMENSION, this.levelOnLoad);
            MinecraftServer server = Services.PLATFORM.getCurrentServer();
            ServerLevel world = server.getLevel(registrykey1);
            if (world != null) {
                entities = entityUUIDs.stream().map(world::getEntity).filter(Objects::nonNull).collect(Collectors.toSet());
            }
        } else {
            return new HashSet<>();
        }
        return new HashSet<>(entities);
    }

    public void removeFollower(LivingEntity entityLiving) {
        this.getFollowers().remove(entityLiving);
    }

    public void tick(int tickCount) {
        if (tickCount % 20 == 0) {
            List<Entity> aliveFollowers = getFollowers().stream().filter(entity -> entity != null && entity.isAlive()).toList();
            setFollowers(aliveFollowers);
        }
    }

    public static final String LEVEL_KEY = "level";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag minions = new ListTag();
        this.getFollowers().forEach(entity -> {
            CompoundTag minion = new CompoundTag();
            minion.putUUID("uuid", entity.getUUID());
            minions.add(minion);
        });
        nbt.put("followers", minions);
        if (!this.getFollowers().isEmpty()) {
            ResourceLocation location = this.getFollowers().get(0).level().dimension().location();
            nbt.putString(LEVEL_KEY, location.toString());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag minionsNBT = tag.getList("followers", 10);
        List<UUID> minionUUIDs = new ArrayList<>();
        for (int i = 0; i < minionsNBT.size(); ++i) {
            CompoundTag compoundnbt = minionsNBT.getCompound(i);
            minionUUIDs.add(compoundnbt.getUUID("uuid"));
        }
        if (tag.contains(LEVEL_KEY)) {
            this.setLevelOnLoad(new ResourceLocation(tag.getString(LEVEL_KEY)));
        }
    }

    public void onEntityJoin(LivingEntity livingEntity) {
        for (Entity entity : getFollowers()) {
            if (entity instanceof Mob mob) {
                Follower follower = FollowerLeaderHelper.getFollowerCapability(mob);
                follower.setLeader(livingEntity);
                FollowerLeaderHelper.addFollowerGoals(mob);
            }
        }
    }
}
