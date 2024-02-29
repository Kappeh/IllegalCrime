package org.kappeh.illegalcrimevelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimecore.IllegalCrimeCore;
import org.slf4j.Logger;

@Plugin(id = IllegalCrimeVelocity.PLUGIN_ID, name = BuildConstants.NAME, version = BuildConstants.VERSION, authors = { "Kappeh" })
public class IllegalCrimeVelocity {
    @NotNull public static final String PLUGIN_ID = "illegalcrimevelocity";

    @NotNull private final Logger logger;
    @NotNull private final ProxyServer proxy;

    @Inject public IllegalCrimeVelocity(@NotNull Logger logger, @NotNull ProxyServer proxy) {
        this.logger = logger;
        this.proxy = proxy;
    }

    @Subscribe public void onProxyInitialization(@NotNull ProxyInitializeEvent event) {
        this.logger.info("Hello, " + IllegalCrimeCore.NAME + "!");
    }
}
