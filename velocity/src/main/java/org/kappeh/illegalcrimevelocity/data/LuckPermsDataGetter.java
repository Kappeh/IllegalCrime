package org.kappeh.illegalcrimevelocity.data;

import com.velocitypowered.api.proxy.Player;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LuckPermsDataGetter implements DataGetter {
    @NotNull private final LuckPerms api;

    public LuckPermsDataGetter() {
        this.api = LuckPermsProvider.get();
    }

    @Override public @NotNull String getFullName(@NotNull Player player) {
        return Optional.ofNullable(this.api.getUserManager().getUser(player.getUniqueId()))
            .map(User::getCachedData)
            .map(data -> {
                final StringBuilder fullName = new StringBuilder();

                final String prefix = data.getMetaData().getPrefix();
                if (prefix != null) {
                    fullName.append(prefix);
                }

                fullName.append(player.getUsername());

                final String suffix = data.getMetaData().getSuffix();
                if (suffix != null) {
                    fullName.append(suffix);
                }

                return fullName.toString();
            })
            .orElse(player.getUsername());
    }
}
