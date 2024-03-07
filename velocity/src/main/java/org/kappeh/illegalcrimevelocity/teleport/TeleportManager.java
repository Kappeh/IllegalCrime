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
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TeleportManager {
    private final @NotNull IllegalCrimeVelocity plugin;

    private final @NotNull ArrayList<TeleportRequest> requests;

    public TeleportManager(final @NotNull IllegalCrimeVelocity plugin) {
        this.plugin = plugin;

        this.requests = new ArrayList<>();
    }

    public final void requestTeleport(
        final @NotNull Player sourcePlayer,
        final @NotNull Player targetPlayer
    ) {
        if (sourcePlayer.equals(targetPlayer)) {
            return;
        }

        final RegisteredServer fromServer = sourcePlayer.getCurrentServer().map(ServerConnection::getServer).orElse(null);
        if (fromServer == null) {
            return;
        }

        final RegisteredServer intoServer = targetPlayer.getCurrentServer().map(ServerConnection::getServer).orElse(null);
        if (intoServer == null) {
            return;
        }

        final TeleportRequest request = new TeleportRequest(
            sourcePlayer.getUniqueId(),
            targetPlayer.getUniqueId(),
            fromServer.getServerInfo().getName(),
            intoServer.getServerInfo().getName()
        );

        this.requests.add(request);

        final Messages.WorldRequest fromWorldRequest = new Messages.WorldRequest(request.getId(), request.getSource());
        final Messages.WorldRequest intoWorldRequest = new Messages.WorldRequest(request.getId(), request.getTarget());

        fromServer.sendPluginMessage(new ChannelId(ChannelIds.TELEPORT_WORLD_INFO.full()), Serialization.serialize(fromWorldRequest));
        intoServer.sendPluginMessage(new ChannelId(ChannelIds.TELEPORT_WORLD_INFO.full()), Serialization.serialize(intoWorldRequest));
    }

    public final void onResponse(final @NotNull Messages.WorldResponse response) {
        int requestIndex = -1;
        for (int i = 0; i < this.requests.size(); i++) {
            if (this.requests.get(i).getId().equals(response.requestId())) {
                requestIndex = i;
                break;
            }
        }

        if (requestIndex == -1) {
            return; // Request has already been processed
        }

        final TeleportRequest request = this.requests.get(requestIndex);
        final Logger logger = this.plugin.getLogger();

        final Player sourcePlayer = this.plugin.getProxy().getPlayer(request.getSource()).orElse(null);
        if (sourcePlayer == null) {
            logger.info(this.requests.toString());
            logger.error(String.format("Internal error while teleporting: Proxy server could not find source player with uuid: %s.", request.getSource()));
            this.requests.remove(requestIndex);
            return;
        }

        final String world = response.world();

        if (response.player().equals(request.getSource())) {
            if (world == null) {
                logger.error(String.format("Internal error while teleporting: Backend server could not find source player with uuid: %s.", request.getSource()));
                sourcePlayer.sendMessage(Component.text("Internal Error: Backend server could not find source player.").color(NamedTextColor.RED));
                this.requests.remove(requestIndex);
                return;
            }
            request.setFromWorld(world);
        }

        if (response.player().equals(request.getTarget())) {
            if (world == null) {
                logger.error(String.format("Internal error while teleporting: Backend server could not find target player with uuid: %s.", request.getTarget()));
                sourcePlayer.sendMessage(Component.text("Internal Error: Backend server could not find target player.").color(NamedTextColor.RED));
                this.requests.remove(requestIndex);
                return;
            }
            request.setIntoWorld(world);
        }

        final TeleportRequest.Ready ready = request.getReady().orElse(null);
        if (ready == null) {
            return; // Waiting for more information
        }

        this.requests.remove(requestIndex);

        final Player targetPlayer = this.plugin.getProxy().getPlayer(request.getTarget()).orElse(null);
        if (targetPlayer == null) {
            logger.error(String.format("Internal error while teleporting: Proxy server could not find target player with uuid: %s.", request.getSource()));
            sourcePlayer.sendMessage(Component.text("Internal Error: Proxy server could not find target player.").color(NamedTextColor.RED));
            return;
        }

        final String fromPermission = String.format("illegalcrime.teleport.from.%s.%s", ready.fromServer(), ready.fromWorld());
        if (!sourcePlayer.hasPermission(fromPermission)) {
            sourcePlayer.sendMessage(Component.text(String.format("Lacking permission %s", fromPermission)).color(NamedTextColor.RED));
            return;
        }

        final String intoPermission = String.format("illegalcrime.teleport.into.%s.%s", ready.intoServer(), ready.intoWorld());
        if (!sourcePlayer.hasPermission(intoPermission)) {
            sourcePlayer.sendMessage(Component.text(String.format("Lacking permission %s", intoPermission)).color(NamedTextColor.RED));
            return;
        }

        this.teleport(sourcePlayer, targetPlayer);
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
