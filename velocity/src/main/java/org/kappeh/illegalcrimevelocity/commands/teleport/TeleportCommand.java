package org.kappeh.illegalcrimevelocity.commands.teleport;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimevelocity.IllegalCrimeVelocity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class TeleportCommand implements SimpleCommand {
    private static final @NotNull String NAME = "illegalteleport";

    private final @NotNull IllegalCrimeVelocity plugin;

    public static void register(
        final @NotNull CommandManager commandManager,
        final @NotNull IllegalCrimeVelocity plugin
    ) {
        final CommandMeta meta = commandManager.metaBuilder(TeleportCommand.NAME)
            .plugin(plugin)
            .build();

        final TeleportCommand command = new TeleportCommand(plugin);

        commandManager.register(meta, command);
    }

    private TeleportCommand(final @NotNull IllegalCrimeVelocity plugin) {
        this.plugin = plugin;
    }

    @Override public CompletableFuture<List<String>> suggestAsync(final @NotNull Invocation invocation) {
        final String[] args = invocation.arguments();
        final ArrayList<String> suggestions = new ArrayList<>();

        if (args.length == 0) {
            for (final Player player: this.plugin.getProxy().getAllPlayers()) {
                final String username = player.getUsername();
                suggestions.add(username);
            }
        }

        if (args.length == 1) {
            final String firstArg = args[0];

            for (final Player player: this.plugin.getProxy().getAllPlayers()) {
                final String username = player.getUsername();
                if (username.startsWith(firstArg)) {
                    suggestions.add(username);
                }
            }
        }

        return CompletableFuture.completedFuture(suggestions);
    }

    @Override public void execute(final @NotNull Invocation invocation) {
        if (invocation.arguments().length == 1) {
            this.executeTeleportToPlayer(invocation);
        } else {
            final Component errorMessage = Component.text("Unknown or incomplete command").color(NamedTextColor.RED);
            invocation.source().sendMessage(errorMessage);
        }
    }

    private void executeTeleportToPlayer(final @NotNull Invocation invocation) {
        final CommandSource source = invocation.source();
        if (!(source instanceof Player sourcePlayer)) {
            source.sendMessage(Component.text("Only players can run this command").color(NamedTextColor.RED));
            return;
        }

        final String targetUsername = invocation.arguments()[0];

        final Player targetPlayer = this.plugin.getProxy().getPlayer(targetUsername).orElse(null);
        if (targetPlayer == null) {
            sourcePlayer.sendMessage(Component.text("Could not find player").color(NamedTextColor.RED));
            return;
        }

        this.plugin.getTeleportManager().teleport(sourcePlayer, targetPlayer);
    }
}
