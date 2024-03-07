package org.kappeh.illegalcrimecore.messages;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class Messages {
    public record WorldRequest(@NotNull UUID id, @NotNull UUID player) {}
    public record WorldResponse(@NotNull UUID requestId, @NotNull UUID player, @Nullable String world) {}

    public record Teleport(@NotNull UUID source, @NotNull UUID target) {}
}
