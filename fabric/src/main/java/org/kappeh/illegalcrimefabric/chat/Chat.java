package org.kappeh.illegalcrimefabric.chat;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;

public class Chat {
    public static void init() {
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register(Chat::onAllowChatMessage);
    }

    private static boolean onAllowChatMessage(SignedMessage message, ServerPlayerEntity player, MessageType.Parameters params) {
        return false;
    }
}
