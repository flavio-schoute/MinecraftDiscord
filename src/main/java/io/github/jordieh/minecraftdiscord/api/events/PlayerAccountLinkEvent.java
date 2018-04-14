/*
 *     This file is part of MinecraftDiscord.
 *
 *     MinecraftDiscord is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MinecraftDiscord is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with MinecraftDiscord.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.jordieh.minecraftdiscord.api.events;

import io.github.jordieh.minecraftdiscord.api.ConnectionRoute;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import sx.blah.discord.handle.obj.IUser;

import java.util.Date;
import java.util.UUID;

public class PlayerAccountLinkEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final ConnectionRoute route;
    @Getter private final OfflinePlayer player;
    @Getter private final long userID;
    @Getter private final IUser user;
    @Getter private final Date date;

    public PlayerAccountLinkEvent(@NonNull UUID uuid, @NonNull IUser user, @NonNull ConnectionRoute route) {
        this.route = route;
        this.player = Bukkit.getOfflinePlayer(uuid);
        this.userID = user.getLongID();
        this.user = user;
        this.date = new Date(System.currentTimeMillis());
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
