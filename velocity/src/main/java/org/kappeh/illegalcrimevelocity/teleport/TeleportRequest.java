package org.kappeh.illegalcrimevelocity.teleport;

import org.checkerframework.common.reflection.qual.GetMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class TeleportRequest {
    private final @NotNull UUID id;

    private final @NotNull UUID source;
    private final @NotNull UUID target;
    private final @NotNull String fromServer;
    private final @NotNull String intoServer;

    private @Nullable String fromWorld;
    private @Nullable String intoWorld;

    public TeleportRequest(
        final @NotNull UUID source,
        final @NotNull UUID target,
        final @NotNull String fromServer,
        final @NotNull String intoServer
    ) {
        this.id = UUID.randomUUID();

        this.source = source;
        this.target = target;
        this.fromServer = fromServer;
        this.intoServer = intoServer;

        this.fromWorld = null;
        this.intoWorld = null;
    }

    @GetMethod public final @NotNull UUID getId() {
        return this.id;
    }

    @GetMethod public final @NotNull UUID getSource() {
        return this.source;
    }

    @GetMethod public final @NotNull UUID getTarget() {
        return this.target;
    }

    @GetMethod public final @NotNull String getFromServer() {
        return this.fromServer;
    }

    @GetMethod public final @NotNull String getIntoServer() {
        return this.intoServer;
    }

    @GetMethod public final @NotNull Optional<String> getFromWorld() {
        return Optional.ofNullable(this.fromWorld);
    }

    @GetMethod public final @NotNull Optional<String> getIntoWorld() {
        return Optional.ofNullable(this.intoWorld);
    }

    public final void setFromWorld(final @NotNull String world) {
        this.fromWorld = world;
    }

    public final void setIntoWorld(final @NotNull String world) {
        this.intoWorld = world;
    }

    public record Ready(
        @NotNull UUID id,

        @NotNull UUID source,
        @NotNull UUID target,

        @NotNull String fromServer,
        @NotNull String fromWorld,
        @NotNull String intoServer,
        @NotNull String intoWorld
    ) {}

    @GetMethod public final @NotNull Optional<TeleportRequest.Ready> getReady() {
        if (this.fromWorld == null || this.intoWorld == null) {
            return Optional.empty();
        }

        return Optional.of(new TeleportRequest.Ready(
            this.id,
            this.source,
            this.target,
            this.fromServer,
            this.fromWorld,
            this.intoServer,
            this.intoWorld
        ));
    }
}
