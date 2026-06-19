package io.github.projectunified.faststats.fabric;

import io.github.projectunified.faststats.core.Config;
import io.github.projectunified.faststats.core.DefaultConfig;
import io.github.projectunified.faststats.core.Metric;
import io.github.projectunified.faststats.core.Platform;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.Collection;

public class FabricPlatform implements Platform {
    private final ModContainer mod;
    private final DefaultConfig config;
    private final MinecraftServer server;

    public FabricPlatform(MinecraftServer server, String modId) throws IllegalStateException {
        this.server = server;

        var fabric = FabricLoader.getInstance();
        mod = fabric.getModContainer(modId).orElseThrow(() -> new IllegalArgumentException("Mod not found: " + modId));

        var dataFolder = fabric.getConfigDir().resolve("faststats");
        var configPath = dataFolder.resolve("config.properties");
        config = DefaultConfig.read(configPath);
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public Collection<Metric<?>> getMetrics() {
        return Arrays.asList(
                Metric.string("minecraft_version", server::getServerVersion),
                Metric.bool("online_mode", server::usesAuthentication),
                Metric.number("player_count", server::getPlayerCount),
                Metric.string("plugin_version", () -> mod.getMetadata().getVersion().getFriendlyString()),
                Metric.string("server_type", () -> "Fabric")
        );
    }
}
