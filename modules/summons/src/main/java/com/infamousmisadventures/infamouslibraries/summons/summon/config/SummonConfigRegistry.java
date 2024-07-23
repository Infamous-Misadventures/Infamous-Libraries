package com.infamousmisadventures.infamouslibraries.summons.summon.config;

import com.infamousmisadventures.infamouslibraries.util.data.CodecJsonDataManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import static com.infamousmisadventures.infamouslibraries.ILConstants.MOD_ID;

public class SummonConfigRegistry {
    public static final ResourceLocation SUMMON_RESOURCELOCATION = new ResourceLocation(MOD_ID, "summon");

    public static final CodecJsonDataManager<SummonConfig> SUMMON_CONFIGS = new CodecJsonDataManager<>(SUMMON_RESOURCELOCATION, "summon", SummonConfig.CODEC);


    public static SummonConfig get(ResourceLocation resourceLocation) {
        return SUMMON_CONFIGS.getData().getOrDefault(resourceLocation, SummonConfig.DEFAULT);
    }

    public static ResourceLocation getKey(SummonConfig config) {
        return SUMMON_CONFIGS.getKey(config);
    }

    public static boolean exists(ResourceLocation resourceLocation) {
        return SUMMON_CONFIGS.getData().containsKey(resourceLocation);
    }

    public static void setData(Map<ResourceLocation, SummonConfig> data) {
        SUMMON_CONFIGS.setData(data);
    }
}