package com.infamousmisadventures.infamouslibraries.abilities;

import com.infamousmisadventures.infamouslibraries.followers.Leader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.infamousmisadventures.infamouslibraries.abilities.AbilityHelper.canApplyToEnemy;
import static com.infamousmisadventures.infamouslibraries.followers.FollowerLeaderHelper.getLeader;
import static com.infamousmisadventures.infamouslibraries.followers.FollowerLeaderHelper.getLeaderCapability;

public class PetHelper {

    public static void makeNearbyPetsAttackTarget(LivingEntity target, LivingEntity owner) {
        if (isPetOf(target, owner) || isPetOf(owner, target))
            return;//don't kill your pets or master!
        Leader leaderCapability = getLeaderCapability(owner);
        List<LivingEntity> nearbyEntities = owner.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(12), nearbyEntity -> isPetOf(owner, nearbyEntity));
        HashSet<Entity> pets = new HashSet<>();
        pets.addAll(leaderCapability.getFollowers());
        pets.addAll(nearbyEntities);
        for (Entity pet : pets) {
            if (pet instanceof Mob) {
                ((Mob) pet).setTarget(target);
            }
        }
    }

    public static boolean canPetAttackEntity(LivingEntity pet, LivingEntity target) {
        return pet.isAlive() && canApplyToEnemy(pet, target);
    }

    public static boolean isPetOf(LivingEntity possibleOwner, LivingEntity possiblePet) {
        return getOwner(possiblePet) == possibleOwner;
    }

    public static boolean isPetOrColleagueRelation(LivingEntity potentialPet1, LivingEntity potentialPet2) {
        LivingEntity owner = getOwner(potentialPet1);
        LivingEntity otherOwner = getOwner(potentialPet2);

        if (owner == null)
            return potentialPet1 == otherOwner;
        if (otherOwner == null)
            return potentialPet2 == owner;
        return owner == otherOwner;
    }

    public static LivingEntity getOwner(LivingEntity potentialPet) {
        LivingEntity owner = null;
        if (potentialPet instanceof TamableAnimal)
            owner = ((TamableAnimal) potentialPet).getOwner();
        if (potentialPet instanceof AbstractHorse)
            owner = getOwnerForHorse((AbstractHorse) potentialPet);
        LivingEntity leader = getLeader(potentialPet);
        if (leader != null)
            owner = leader;
        return owner;
    }

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

}
