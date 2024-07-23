package com.infamousmisadventures.infamouslibraries.summons.summon;

import com.infamousmisadventures.infamouslibraries.general.INBTSerializable;
import com.infamousmisadventures.infamouslibraries.summons.SummonHelper;
import com.infamousmisadventures.infamouslibraries.summons.summon.config.SummonConfig;
import com.infamousmisadventures.infamouslibraries.summons.summon.config.SummonConfigRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;


public class Summon implements INBTSerializable<CompoundTag> {

    private boolean summon = false;
    private SummonConfig config = SummonConfig.DEFAULT;
    private boolean goalsAdded = false;

    public boolean isSummon() {
        return this.summon == true;
    }

    public void setSummon(boolean summon) {
        this.summon = summon;
    }

    public void setConfig(SummonConfig config) {
        this.summon = true;
        this.config = config;
    }

    public SummonConfig getConfig() {
        return config;
    }

    public boolean isGoalsAdded() {
        return goalsAdded;
    }

    public void setGoalsAdded(boolean goalsAdded) {
        this.goalsAdded = goalsAdded;
    }

    public static final String SUMMON_FLAG_KEY = "is_summon";
    public static final String CONFIG_FLAG_KEY = "config";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(SUMMON_FLAG_KEY, this.isSummon());
        tag.putString(CONFIG_FLAG_KEY, SummonConfigRegistry.getKey(this.getConfig()).toString());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains(SUMMON_FLAG_KEY)) {
            this.setSummon(tag.getBoolean(SUMMON_FLAG_KEY));
        }
        if (tag.contains(CONFIG_FLAG_KEY)) {
            this.setConfig(SummonConfigRegistry.get(new ResourceLocation(tag.getString(CONFIG_FLAG_KEY))));
        }
    }

    public void onEntityJoin(Mob mob) {
        SummonHelper.addSummonGoals(mob);
    }
}
