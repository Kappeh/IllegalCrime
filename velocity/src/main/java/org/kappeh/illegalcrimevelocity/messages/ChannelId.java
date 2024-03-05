package org.kappeh.illegalcrimevelocity.messages;

import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import org.jetbrains.annotations.NotNull;

public record ChannelId(@NotNull String id) implements ChannelIdentifier {
    @Override public @NotNull String getId() {
        return this.id;
    }
}
