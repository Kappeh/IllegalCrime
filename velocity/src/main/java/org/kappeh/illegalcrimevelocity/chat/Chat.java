package org.kappeh.illegalcrimevelocity.chat;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimevelocity.IllegalCrimeVelocity;
import org.kappeh.illegalcrimevelocity.PluginIds;
import org.slf4j.Logger;

public class Chat {
    private static final int MAX_HISTORY_SIZE = 100;

    @NotNull private final IllegalCrimeVelocity plugin;
    @NotNull private final ChatHistory history;

    public static boolean tryInit(@NotNull final IllegalCrimeVelocity plugin) {
        final ProxyServer proxy = plugin.getProxy();
        final Logger logger = plugin.getLogger();

        final PluginManager pluginManager = proxy.getPluginManager();
        final boolean unsignedVelocityPresent = pluginManager.getPlugin(PluginIds.UNSIGNED_VELOCITY).isPresent();
        final boolean signedVelocityPresent = pluginManager.getPlugin(PluginIds.SIGNED_VELOCITY).isPresent();

        if (unsignedVelocityPresent && signedVelocityPresent) {
            logger.error("Detected both UnsignedVelocity and SignedVelocity! Please uninstall UnsignedVelocity.");
            return false;
        }

        if (unsignedVelocityPresent) {
            logger.warn("UnsignedVelocity detected. UnsignedVelocity is deprecated. Please install SignedVelocity for better support.");
        } else if (!signedVelocityPresent) {
            logger.warn("Neither UnsignedVelocity nor SignedVelocity were detected. Please install SignedVelocity for 1.19+ support.");
        }

        proxy.getEventManager().register(plugin, new Chat(plugin));

        return true;
    }

    private Chat(@NotNull final IllegalCrimeVelocity plugin) {
        this.plugin = plugin;
        this.history = new ChatHistory(Chat.MAX_HISTORY_SIZE);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Subscribe private void onPlayerChat(@NotNull final PlayerChatEvent event) {
        event.setResult(PlayerChatEvent.ChatResult.denied());

        final Player sender = event.getPlayer();
        final String message = event.getMessage();

        this.plugin.getLogger().info(String.format("[%s]: %s", sender.getUsername(), message));

        final Component formattedMessage = Component.text()
            .append(MineDown.parse(this.plugin.getDataGetter().getFullName(sender)))
            .append(Component.text().content(": ").color(NamedTextColor.WHITE))
            .append(MineDown.parse(message))
            .build();

        for (final Player player: this.plugin.getProxy().getAllPlayers()) {
            player.sendMessage(formattedMessage);
        }

        this.history.push(formattedMessage);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Subscribe private void onPreLogin(@NotNull final PreLoginEvent event) {
        final String username = event.getUsername();

        final Component joinMessage = Component.text()
            .content(String.format("%s joined.", username))
            .color(NamedTextColor.YELLOW)
            .build();

        for (final Player player: this.plugin.getProxy().getAllPlayers()) {
            player.sendMessage(joinMessage);
        }

        this.history.push(joinMessage);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Subscribe private void onDisconnect(@NotNull final DisconnectEvent event) {
        final String username = event.getPlayer().getUsername();

        final Component leaveMessage = Component.text()
            .content(String.format("%s left.", username))
            .color(NamedTextColor.YELLOW)
            .build();

        for (final Player player: this.plugin.getProxy().getAllPlayers()) {
            player.sendMessage(leaveMessage);
        }

        this.history.push(leaveMessage);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Subscribe private void onServerConnected(@NotNull final ServerConnectedEvent event) {
        final Player player = event.getPlayer();
        for (final Component message: this.history) {
            player.sendMessage(message);
        }
    }
}
