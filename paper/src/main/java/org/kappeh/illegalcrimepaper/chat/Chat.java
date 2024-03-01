package org.kappeh.illegalcrimepaper.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimepaper.IllegalCrimePaper;

public class Chat implements Listener {
    public static void init(IllegalCrimePaper plugin) {
        plugin.getServer().getPluginManager().registerEvents(new Chat(), plugin);
    }

    @EventHandler public void onChat(@NotNull AsyncChatEvent event) {
        event.setCancelled(true);
    }
}
