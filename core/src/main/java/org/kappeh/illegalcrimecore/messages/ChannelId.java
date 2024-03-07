package org.kappeh.illegalcrimecore.messages;

import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimecore.IllegalCrimeCore;

public record ChannelId(@NotNull String namespace, @NotNull String path) {
    public @NotNull String full() {
        return String.format("%s:%s", this.namespace, this.path);
    }

    public static @NotNull ChannelId custom(final @NotNull String path) {
        return new ChannelId(IllegalCrimeCore.ID, path);
    }
}
