package com.infamousmisadventures.infamouslibraries;

import net.fabricmc.api.ModInitializer;

public class InfamousLibrariesFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        InfamousLibraries.init();
        setupDatapackFormats();
    }

    private void setupDatapackFormats() {
        //CodecDataManagerSync.subscribeAsSyncable(ArtifactGearConfigSyncPacket::new, EXAMPLE_DATAPACK);
    }
}
