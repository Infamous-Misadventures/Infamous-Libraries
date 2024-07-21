package com.infamousmisadventures.infamouslibraries.util;

import net.minecraft.resources.ResourceLocation;

public class ResourceLocationHelper {
    public static ResourceLocation mcLoc(String path) {
        return new ResourceLocation(path);
    }

    public static ResourceLocation modLoc(String modid, String path) {
        return new ResourceLocation(modid, path);
    }

    public static ResourceLocation forgeLoc(String path) {
        return new ResourceLocation("forge", path);
    }
}
