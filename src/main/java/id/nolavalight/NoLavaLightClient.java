package id.nolavalight;

import net.fabricmc.api.ClientModInitializer;

public class NoLavaLightClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LavaLightFix.init();
    }
}
