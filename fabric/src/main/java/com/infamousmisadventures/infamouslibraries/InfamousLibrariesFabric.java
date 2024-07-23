package com.infamousmisadventures.infamouslibraries;

import com.infamousmisadventures.infamouslibraries.platform.Services;
import com.infamousmisadventures.infamouslibraries.platform.services.FabricPlatformHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class InfamousLibrariesFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        InfamousLibraries.init();
        setupDatapackFormats();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ((FabricPlatformHelper) Services.PLATFORM).registerServer(server);
        });
    }

    private void setupDatapackFormats() {
        //CodecDataManagerSync.subscribeAsSyncable(ArtifactGearConfigSyncPacket::new, EXAMPLE_DATAPACK);
    }
}
