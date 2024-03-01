package org.kappeh.illegalcrimefabric;

import net.fabricmc.api.ModInitializer;
import org.kappeh.illegalcrimecore.IllegalCrimeCore;
import org.kappeh.illegalcrimefabric.chat.Chat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllegalCrimeFabric implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("illegalcrimefabric");

    @Override public void onInitialize() {
        IllegalCrimeFabric.LOGGER.info("Hello, " + IllegalCrimeCore.NAME + "!");

        Chat.init();
    }
}
