package com.infamousmisadventures.infamouslibraries.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public interface IRegistryCreator {
    void setupRegistryCreator();

    <P> Registry<P> createRegistry(Class<P> type, ResourceLocation registryName);

}
