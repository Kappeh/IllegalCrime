package org.kappeh.illegalcrimefabric.teleport;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.kappeh.illegalcrimecore.messages.ChannelIds;
import org.kappeh.illegalcrimecore.messages.Messages;
import org.kappeh.illegalcrimecore.messages.Serialization;
import org.kappeh.illegalcrimefabric.IllegalCrimeFabric;
import org.kappeh.illegalcrimefabric.utils.Utils;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public final class TeleportManager {
    private final @NotNull ArrayList<Messages.Teleport> pending;
    private final @NotNull Identifier worldInfoChannelId;
    private final @NotNull Identifier teleportChannelId;

    public static void init() {
        TeleportManager teleportManager = new TeleportManager();
        if (!ServerPlayNetworking.registerGlobalReceiver(teleportManager.worldInfoChannelId, teleportManager::onWorldInfoMessage)) {
            IllegalCrimeFabric.getLogger().error("Failed to initialize teleport manager: World info handler already listening to channel");
            return;
        }
        if (!ServerPlayNetworking.registerGlobalReceiver(teleportManager.teleportChannelId, teleportManager::onTeleportMessage)) {
            IllegalCrimeFabric.getLogger().error("Failed to initialize teleport manager: Teleport handler already listening to channel");
            return;
        }
        ServerPlayConnectionEvents.JOIN.register(Event.DEFAULT_PHASE, teleportManager::handleJoin);
    }

    private TeleportManager() {
        this.pending = new ArrayList<>();
        this.worldInfoChannelId = new Identifier(ChannelIds.TELEPORT_WORLD_INFO.namespace(), ChannelIds.TELEPORT_WORLD_INFO.path());
        this.teleportChannelId = new Identifier(ChannelIds.TELEPORT.namespace(), ChannelIds.TELEPORT.path());
    }

    private void onWorldInfoMessage(
        final @NotNull MinecraftServer server,
        final @NotNull ServerPlayerEntity player,
        final @NotNull ServerPlayNetworkHandler handler,
        final @NotNull PacketByteBuf in,
        final @NotNull PacketSender responseSender
    ) {
        final byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        final Messages.WorldRequest request = Serialization.deserialize(bytes, Messages.WorldRequest.class);

        final PlayerEntity requestPlayer = server.getPlayerManager().getPlayer(request.player());
        final Messages.WorldResponse response = new Messages.WorldResponse(
            request.id(),
            request.player(),
            Optional.ofNullable(requestPlayer).map(p -> Utils.getWorldName(p.getWorld())).orElse(null)
        );

        final byte[] responseBytes = Serialization.serialize(response);
        PacketByteBuf out = PacketByteBufs.create();
        out.writeBytes(responseBytes);

        ServerPlayNetworking.send(player, this.worldInfoChannelId, out);
    }

    private void onTeleportMessage(
        final @NotNull MinecraftServer server,
        final @NotNull ServerPlayerEntity player,
        final @NotNull ServerPlayNetworkHandler handler,
        final @NotNull PacketByteBuf buf,
        final @NotNull PacketSender responseSender
    ) {
        final byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        final Messages.Teleport message = Serialization.deserialize(bytes, Messages.Teleport.class);

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
