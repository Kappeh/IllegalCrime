package org.kappeh.illegalcrimepaper;

import org.bukkit.plugin.java.JavaPlugin;
import org.kappeh.illegalcrimecore.IllegalCrimeCore;
import org.kappeh.illegalcrimepaper.chat.Chat;

public final class IllegalCrimePaper extends JavaPlugin {
    @Override public void onEnable() {
        this.getLogger().info("Hello, " + IllegalCrimeCore.NAME + "!");

        Chat.init(this);
    }

    @Override public void onDisable() {

    }
}
