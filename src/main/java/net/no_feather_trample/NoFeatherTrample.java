package net.no_feather_trample;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

public final class NoFeatherTrample implements ModInitializer {
    public static final String MOD_ID = "no_feather_trample";

    @Override
    public void onInitialize() {
        String version = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow()
                .getMetadata().getVersion().getFriendlyString();
        LoggerFactory.getLogger(MOD_ID).info("[No Feather Trample] Mod initialized. Version: {}", version);
    }
}
