package org.kappeh.illegalcrimepaper.teleport;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimecore.messages.ChannelIds;
import org.kappeh.illegalcrimecore.messages.Messages;
import org.kappeh.illegalcrimecore.messages.Serialization;
import org.kappeh.illegalcrimepaper.IllegalCrimePaper;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public final class TeleportManager implements PluginMessageListener, Listener {
    private final @NotNull IllegalCrimePaper plugin;

    private final @NotNull ArrayList<Messages.Teleport> pending;

    public static void init(final @NotNull IllegalCrimePaper plugin) {
        final TeleportManager teleportManager = new TeleportManager(plugin);

        final Server server = plugin.getServer();
        final Messenger messenger = server.getMessenger();

        messenger.registerIncomingPluginChannel(plugin, ChannelIds.TELEPORT_WORLD_INFO.full(), teleportManager);
        messenger.registerOutgoingPluginChannel(plugin, ChannelIds.TELEPORT_WORLD_INFO.full());
        messenger.registerIncomingPluginChannel(plugin, ChannelIds.TELEPORT.full(), teleportManager);

        plugin.getServer().getPluginManager().registerEvents(teleportManager, plugin);
    }

    private TeleportManager(final @NotNull IllegalCrimePaper plugin) {
        this.plugin = plugin;
        this.pending = new ArrayList<>();
    }

    @Override public void onPluginMessageReceived(
        final @NotNull String channel,
        final @NotNull Player player,
        final byte @NotNull [] bytes
    ) {
        if (channel.equals(ChannelIds.TELEPORT_WORLD_INFO.full())) {
            final Messages.WorldRequest request = Serialization.deserialize(bytes, Messages.WorldRequest.class);
            final Messages.WorldResponse response = this.onWorldInfoMessage(request);
            this.plugin.getServer().sendPluginMessage(this.plugin, channel, Serialization.serialize(response));
        } else if (channel.equals(ChannelIds.TELEPORT.full())) {
            this.onTeleportMessage(Serialization.deserialize(bytes, Messages.Teleport.class));
        }
    }

    private @NotNull Messages.WorldResponse onWorldInfoMessage(final @NotNull Messages.WorldRequest request) {
        final Player player = this.plugin.getServer().getPlayer(request.player());
        return new Messages.WorldResponse(
            request.id(),
            request.player(),
            Optional.ofNullable(player).map(p -> p.getWorld().getName()).orElse(null)
        );
    }

    private void onTeleportMessage(final @NotNull Messages.Teleport message) {
        final Server server = this.plugin.getServer();

        final Player sourcePlayer = server.getPlayer(message.source());
        if (sourcePlayer == null) {
            this.pending.add(message);
            return;
        }

        final Player targetPlayer = server.getPlayer(message.target());
        if (targetPlayer == null) {
            this.plugin.getLogger().severe("Could not find target player with uuid: " + message.target());
            return;
        }

        sourcePlayer.teleport(targetPlayer.getLocation());
    }

    @EventHandler public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        final Server server = this.plugin.getServer();

        final Player sourcePlayer = event.getPlayer();
        final UUID sourcePlayerUuid = sourcePlayer.getUniqueId();

        final ArrayList<Messages.Teleport> handled = new ArrayList<>();

        for (final Messages.Teleport message: this.pending) {
            if (!(message.source().equals(sourcePlayerUuid))) {
                continue;
            }

            handled.add(message);

            final Player targetPlayer = server.getPlayer(message.target());
            if (targetPlayer == null) {
                this.plugin.getLogger().severe("Could not find target player with uuid: " + message.target());
                continue;
            }

            sourcePlayer.teleport(targetPlayer.getLocation());
        }

        this.pending.removeAll(handled);
    }
}
