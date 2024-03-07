package org.kappeh.illegalcrimepaper.commands;

import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimepaper.IllegalCrimePaper;

public final class Commands {
    public static void register(final @NotNull IllegalCrimePaper plugin) {
        FooCommand.register(plugin);
    }
}
