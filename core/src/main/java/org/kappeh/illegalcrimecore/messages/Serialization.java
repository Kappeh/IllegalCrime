package org.kappeh.illegalcrimecore.messages;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public final class Serialization {
    private static final @NotNull Gson gson = new Gson();

    public static <T> byte @NotNull [] serialize(T object) {
        String json = Serialization.gson.toJson(object);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    public static <T> @NotNull T deserialize(byte @NotNull [] bytes, Class<T> type) throws JsonSyntaxException {
        String json = new String(bytes, StandardCharsets.UTF_8);
        return Serialization.gson.fromJson(json, type);
    }
}
