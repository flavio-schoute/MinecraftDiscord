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

package io.github.jordieh.minecraftdiscord.common;

import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import sx.blah.discord.handle.obj.IUser;

import java.util.Optional;
import java.util.UUID;

public class UserPair extends Pair<Long, UUID> {

    public UserPair(Long left, UUID right) {
        super(left, right);
    }

    public UserPair() {
        super();
    }

    public final Optional<IUser> getUser() {
        return Optional.ofNullable(ClientHandler.getInstance().getClient().getUserByID(super.getLeft()));
    }

    public final OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(super.getRight());
    }
}
