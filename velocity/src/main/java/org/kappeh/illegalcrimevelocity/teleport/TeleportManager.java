package org.kappeh.illegalcrimevelocity.teleport;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimecore.messages.ChannelIds;
import org.kappeh.illegalcrimecore.messages.Messages;
import org.kappeh.illegalcrimecore.messages.Serialization;
import org.kappeh.illegalcrimevelocity.IllegalCrimeVelocity;
import org.kappeh.illegalcrimevelocity.messages.ChannelId;

import java.nio.charset.StandardCharsets;

public class TeleportManager {
    private final @NotNull IllegalCrimeVelocity plugin;

    public TeleportManager(final @NotNull IllegalCrimeVelocity plugin) {
        this.plugin = plugin;
    }

    public final void teleport(
        final @NotNull Player sourcePlayer,
        final @NotNull Player targetPlayer
    ) {
        final RegisteredServer sourceServer = sourcePlayer.getCurrentServer().map(ServerConnection::getServer).orElse(null);
        if (sourceServer == null) {
            sourcePlayer.sendMessage(Component.text("Could not find source server").color(NamedTextColor.RED));
            return;
        }

        final RegisteredServer targetServer = targetPlayer.getCurrentServer().map(ServerConnection::getServer).orElse(null);
        if (targetServer == null) {
            sourcePlayer.sendMessage(Component.text("Target server not found").color(NamedTextColor.RED));
            return;
        }

        if (!sourceServer.equals(targetServer)) {
            sourcePlayer.createConnectionRequest(targetServer).fireAndForget();
        }

        final Messages.Teleport message = new Messages.Teleport(sourcePlayer.getUniqueId(), targetPlayer.getUniqueId());
        final byte[] bytes = Serialization.serialize(message);
        final String messageString = new String(bytes, StandardCharsets.UTF_8);
        this.plugin.getLogger().info(messageString);

        targetServer.sendPluginMessage(new ChannelId(ChannelIds.TELEPORT.full()), bytes);
    }
}
