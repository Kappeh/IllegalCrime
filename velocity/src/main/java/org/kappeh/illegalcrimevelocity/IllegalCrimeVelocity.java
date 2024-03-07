package org.kappeh.illegalcrimevelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelMessageSource;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.checkerframework.common.reflection.qual.GetMethod;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimecore.IllegalCrimeCore;
import org.kappeh.illegalcrimecore.messages.ChannelIds;
import org.kappeh.illegalcrimecore.messages.Messages;
import org.kappeh.illegalcrimecore.messages.Serialization;
import org.kappeh.illegalcrimevelocity.chat.Chat;
import org.kappeh.illegalcrimevelocity.commands.Commands;
import org.kappeh.illegalcrimevelocity.data.DataGetter;
import org.kappeh.illegalcrimevelocity.data.DefaultDataGetter;
import org.kappeh.illegalcrimevelocity.data.LuckPermsDataGetter;
import org.kappeh.illegalcrimevelocity.teleport.TeleportManager;
import org.slf4j.Logger;

@Plugin(
    id = PluginIds.ILLEGAL_CRIME_VELOCITY,
    name = BuildConstants.NAME,
    version = BuildConstants.VERSION,
    authors = {"Kappeh"},
    dependencies = {
        @Dependency(id = PluginIds.LUCK_PERMS, optional = true),
    }
)
public final class IllegalCrimeVelocity {
    private final @NotNull Logger logger;
    private final @NotNull ProxyServer proxy;

    private @NotNull DataGetter dataGetter;
    private final @NotNull TeleportManager teleportManager;

    @Inject public IllegalCrimeVelocity(final @NotNull Logger logger, final @NotNull ProxyServer proxy) {
        this.logger = logger;
        this.proxy = proxy;

        this.dataGetter = new DefaultDataGetter();
        this.teleportManager = new TeleportManager(this);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Subscribe public void onProxyInitialization(final @NotNull ProxyInitializeEvent event) {
        this.logger.info("Hello, " + IllegalCrimeCore.NAME + "!");

        if (this.proxy.getPluginManager().getPlugin(PluginIds.LUCK_PERMS).isPresent()) {
            this.dataGetter = new LuckPermsDataGetter();
        }

        if (!Chat.tryInit(this)) {
            this.logger.error("Chat features have been disabled!");
        }

        Commands.register(this);

        this.proxy.getChannelRegistrar().register(MinecraftChannelIdentifier.from(ChannelIds.TELEPORT_WORLD_INFO.full()));
    }

    @Subscribe void onPluginMessageFromBackend(PluginMessageEvent event) {
        final ChannelMessageSource source =  event.getSource();
        if (!(source instanceof ServerConnection serverConnection)) {
            return;
        }

        if (event.getIdentifier().getId().equals(ChannelIds.TELEPORT_WORLD_INFO.full())) {
            final Messages.WorldResponse response = Serialization.deserialize(event.getData(), Messages.WorldResponse.class);
            this.teleportManager.onResponse(response);
        }
    }

    @GetMethod public @NotNull Logger getLogger() {
        return this.logger;
    }

    @GetMethod public @NotNull ProxyServer getProxy() {
        return this.proxy;
    }

    @GetMethod public @NotNull DataGetter getDataGetter() {
        return this.dataGetter;
    }

    @GetMethod public @NotNull TeleportManager getTeleportManager() {
        return this.teleportManager;
    }
}
