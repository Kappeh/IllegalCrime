package org.kappeh.illegalcrimefabric;

import net.fabricmc.api.ModInitializer;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimecore.IllegalCrimeCore;
import org.kappeh.illegalcrimefabric.chat.Chat;
import org.kappeh.illegalcrimefabric.teleport.TeleportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllegalCrimeFabric implements ModInitializer {
    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger("illegalcrimefabric");

    @Override public void onInitialize() {
        IllegalCrimeFabric.LOGGER.info("Hello, " + IllegalCrimeCore.NAME + "!");

        Chat.init();
        TeleportManager.init();
    }

    public static @NotNull Logger getLogger() {
        return IllegalCrimeFabric.LOGGER;
    }
}
