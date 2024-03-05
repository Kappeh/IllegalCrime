package org.kappeh.illegalcrimecore.messages;

import org.jetbrains.annotations.NotNull;

public record ChannelId(@NotNull String namespace, @NotNull String path) {
    public @NotNull String full() {
        return String.format("%s:%s", this.namespace, this.path);
    }
}
