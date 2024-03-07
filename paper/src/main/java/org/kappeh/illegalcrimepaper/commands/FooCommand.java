package org.kappeh.illegalcrimepaper.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimepaper.IllegalCrimePaper;

import java.util.Objects;
import java.util.stream.Collectors;

public class FooCommand implements CommandExecutor {
    private final @NotNull IllegalCrimePaper plugin;

    public static void register(final @NotNull IllegalCrimePaper plugin) {
        Objects.requireNonNull(plugin.getCommand("foo")).setExecutor(new FooCommand(plugin));
    }

    private FooCommand(final @NotNull IllegalCrimePaper plugin) {
        this.plugin = plugin;
    }

    @Override public boolean onCommand(
        final @NotNull CommandSender commandSender,
        final @NotNull Command command,
        final @NotNull String s,
        final @NotNull String[] strings
    ) {
        final String worlds = this.plugin.getServer().getWorlds().stream()
            .map(WorldInfo::getName)
            .collect(Collectors.joining(" "));

        commandSender.sendMessage(Component.text(worlds).color(NamedTextColor.YELLOW));

        return true;
    }
}
