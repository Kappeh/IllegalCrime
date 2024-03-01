package org.kappeh.illegalcrimevelocity.data;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

public interface DataGetter {
    @NotNull String getFullName(Player player);
}
