package org.kappeh.illegalcrimevelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.checkerframework.common.reflection.qual.GetMethod;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimecore.IllegalCrimeCore;
import org.kappeh.illegalcrimevelocity.chat.Chat;
import org.kappeh.illegalcrimevelocity.data.DataGetter;
import org.kappeh.illegalcrimevelocity.data.DefaultDataGetter;
import org.kappeh.illegalcrimevelocity.data.LuckPermsDataGetter;
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
public class IllegalCrimeVelocity {
    @NotNull private final Logger logger;
    @NotNull private final ProxyServer proxy;

    @NotNull private DataGetter dataGetter;

    @Inject public IllegalCrimeVelocity(@NotNull Logger logger, @NotNull ProxyServer proxy) {
        this.logger = logger;
        this.proxy = proxy;

        this.dataGetter = new DefaultDataGetter();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Subscribe public void onProxyInitialization(@NotNull ProxyInitializeEvent event) {
        this.logger.info("Hello, " + IllegalCrimeCore.NAME + "!");

        if (this.proxy.getPluginManager().getPlugin(PluginIds.LUCK_PERMS).isPresent()) {
            this.dataGetter = new LuckPermsDataGetter();
        }

        if (!Chat.tryInit(this)) {
            this.logger.error("Chat features have been disabled!");
        }
    }

    @GetMethod @NotNull public final Logger getLogger() {
        return this.logger;
    }

    @GetMethod @NotNull public final ProxyServer getProxy() {
        return this.proxy;
    }

    @GetMethod @NotNull public final DataGetter getDataGetter() {
        return this.dataGetter;
    }
}
