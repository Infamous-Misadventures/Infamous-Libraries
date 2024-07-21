package com.infamousmisadventures.infamouslibraries;

import com.infamousmisadventures.infamouslibraries.datapack.DatapackReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ILConstants.MOD_ID)
public class InfamousLibrariesForge {
    
    public InfamousLibrariesForge() {
        InfamousLibraries.init();
        setupDatapackFormats();
        setupEvents();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }

    private void setupDatapackFormats() {
        //CodecDataManagerSync.subscribeAsSyncable(ExampleSyncPacket::new, EXAMPLE_DATAPACK);
    }

    public void setupEvents() {
        MinecraftForge.EVENT_BUS.addListener(DatapackReloadListener::onAddReloadListeners);
    }

    private void setupClient(final FMLClientSetupEvent event) {
        event.enqueueWork(InfamousLibrariesForgeClient::setupClient);
    }
}