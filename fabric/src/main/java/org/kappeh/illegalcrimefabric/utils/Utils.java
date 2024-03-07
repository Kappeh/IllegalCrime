package org.kappeh.illegalcrimefabric.utils;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public final class Utils {
    public static @NotNull String getWorldName(final @NotNull World world) {
        RegistryKey<World> registryKey = world.getRegistryKey();
        if (registryKey == World.OVERWORLD) {
            final MinecraftServer server = world.getServer();
            if (server != null) {
                return server.getSaveProperties().getLevelName();
            }
        } else if (registryKey == World.END) {
            return "DIM1";
        } else if (registryKey == World.NETHER) {
            return "DIM-1";
        }
        return registryKey.getValue().getNamespace() + "_" + registryKey.getValue().getPath();
    }
}
