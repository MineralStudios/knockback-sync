package me.caseload.knockbacksync;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.server.MinecraftServer;

public class KBSyncFabricLoaderMod implements PreLaunchEntrypoint, ModInitializer {

    private final KnockbackSyncBase core = new KBSyncFabricBase();

    public static MinecraftServer getServer() {
        return (MinecraftServer) FabricLoader.getInstance().getGameInstance();
    }

    @Override
    public void onPreLaunch() {
        core.load();
    }

    @Override
    public void onInitialize() {
        core.enable();
        core.initializeScheduler();
        core.configManager.loadConfig(false);
        core.statsManager.init();
        core.checkForUpdates();
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            core.scheduler.shutdown();
            core.statsManager.getMetrics().shutdown();
        });
    }
}
