package org.kappeh.illegalcrimecore.messages;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class Messages {
    public record Teleport(@NotNull UUID source, @NotNull UUID target) {}
}
