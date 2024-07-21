package com.infamousmisadventures.infamouslibraries;

import com.infamousmisadventures.infamouslibraries.platform.Services;

public class InfamousLibraries {

    public static void init() {
        Services.REGISTRAR.setupRegistrar();
        Services.REGISTRY_CREATOR.setupRegistryCreator();
        Services.NETWORK_HANDLER.setupNetworkHandler();
    }
}