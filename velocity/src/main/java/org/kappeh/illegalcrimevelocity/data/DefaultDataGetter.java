package org.kappeh.illegalcrimevelocity.data;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultDataGetter implements DataGetter {
    public DefaultDataGetter() {}

    @Override public @NotNull String getFullName(Player player) {
        return player.getUsername();
    }
}
