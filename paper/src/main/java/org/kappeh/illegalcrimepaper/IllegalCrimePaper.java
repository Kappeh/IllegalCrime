package org.kappeh.illegalcrimepaper;

import org.bukkit.plugin.java.JavaPlugin;
import org.kappeh.illegalcrimecore.IllegalCrimeCore;

public final class IllegalCrimePaper extends JavaPlugin {
    @Override public void onEnable() {
        this.getLogger().info("Hello, " + IllegalCrimeCore.NAME + "!");
    }

    @Override public void onDisable() {

    }
}
