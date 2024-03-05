package org.kappeh.illegalcrimevelocity.commands;

import com.velocitypowered.api.command.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimevelocity.IllegalCrimeVelocity;
import org.kappeh.illegalcrimevelocity.commands.teleport.TeleportCommand;

public final class Commands {
    public static void register(@NotNull final IllegalCrimeVelocity plugin) {
        final CommandManager commandManager = plugin.getProxy().getCommandManager();

        TeleportCommand.register(commandManager, plugin);
    }
}
