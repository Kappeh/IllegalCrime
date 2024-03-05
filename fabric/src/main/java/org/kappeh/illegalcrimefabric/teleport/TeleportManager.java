package org.kappeh.illegalcrimefabric.teleport;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kappeh.illegalcrimecore.messages.ChannelIds;
import org.kappeh.illegalcrimecore.messages.Messages;
import org.kappeh.illegalcrimecore.messages.Serialization;
import org.kappeh.illegalcrimefabric.IllegalCrimeFabric;

import java.util.ArrayList;
import java.util.UUID;

public final class TeleportManager {
    private final @NotNull ArrayList<Messages.Teleport> pending;
    private final @NotNull Identifier channel_id;

    public static void init() {
        TeleportManager teleportManager = new TeleportManager();
        if (!ServerPlayNetworking.registerGlobalReceiver(teleportManager.channel_id, teleportManager::handleGlobalReceived)) {
            IllegalCrimeFabric.getLogger().error("Failed to initialize teleport manager: message channel already being listened on.");
            return;
        }
        ServerPlayConnectionEvents.JOIN.register(Event.DEFAULT_PHASE, teleportManager::handleJoin);
    }

    private TeleportManager() {
        this.pending = new ArrayList<>();
        this.channel_id = new Identifier(ChannelIds.TELEPORT.namespace(), ChannelIds.TELEPORT.path());
    }

    private void handleGlobalReceived(
        final @NotNull MinecraftServer server,
        final @Nullable ServerPlayerEntity player,
        final @NotNull ServerPlayNetworkHandler handler,
        final @NotNull PacketByteBuf buf,
        final @NotNull PacketSender responseSender
    ) {
        final byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        Messages.Teleport message = Serialization.deserialize(bytes, Messages.Teleport.class);

        final PlayerManager playerManager = server.getPlayerManager();

        final ServerPlayerEntity sourcePlayer =  playerManager.getPlayer(message.source());
        if (sourcePlayer == null) {
            this.pending.add(message);
            return;
        }

        final ServerPlayerEntity targetPlayer = playerManager.getPlayer(message.target());
        if (targetPlayer == null) {
            IllegalCrimeFabric.getLogger().error("Could not find target player with uuid: " + message.target());
            return;
        }

        final ServerWorld targetWorld = targetPlayer.getServerWorld();
        sourcePlayer.teleport(targetWorld, targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(), targetPlayer.getYaw(), targetPlayer.getPitch());
    }

    private void handleJoin(
        final @NotNull ServerPlayNetworkHandler handler,
        final @NotNull PacketSender sender,
        final @NotNull MinecraftServer server
    ) {
        final PlayerManager playerManager = server.getPlayerManager();

        final ServerPlayerEntity sourcePlayer =  handler.player;
        final UUID sourcePlayerUuid = handler.player.getUuid();

        IllegalCrimeFabric.getLogger().info("Checking pending teleports for player with uuid: " + sourcePlayerUuid);

        ArrayList<Messages.Teleport> handled = new ArrayList<>();

        for (final Messages.Teleport message: this.pending) {
            if (!(message.source().equals(sourcePlayerUuid))) {
                continue;
            }

            handled.add(message);

            final ServerPlayerEntity targetPlayer = playerManager.getPlayer(message.target());
            if (targetPlayer == null) {
                IllegalCrimeFabric.getLogger().error("Could not find target player with uuid: " + message.target());
                continue;
            }

            final ServerWorld targetWorld = targetPlayer.getServerWorld();
            sourcePlayer.teleport(targetWorld, targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(), targetPlayer.getYaw(), targetPlayer.getPitch());
        }

        this.pending.removeAll(handled);
    }
}
