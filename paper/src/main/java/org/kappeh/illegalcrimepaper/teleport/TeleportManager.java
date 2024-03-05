package org.kappeh.illegalcrimepaper.teleport;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimecore.messages.ChannelIds;
import org.kappeh.illegalcrimecore.messages.Messages;
import org.kappeh.illegalcrimecore.messages.Serialization;
import org.kappeh.illegalcrimepaper.IllegalCrimePaper;

import java.util.ArrayList;
import java.util.UUID;

public final class TeleportManager implements PluginMessageListener, Listener {
    private final @NotNull IllegalCrimePaper plugin;

    private final @NotNull ArrayList<Messages.Teleport> pending;

    public static void init(final @NotNull IllegalCrimePaper plugin) {
        TeleportManager teleportManager = new TeleportManager(plugin);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, ChannelIds.TELEPORT.full(), teleportManager);
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
        if (!channel.equals(ChannelIds.TELEPORT.full())) {
            return;
        }

        final Messages.Teleport message = Serialization.deserialize(bytes, Messages.Teleport.class);
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
