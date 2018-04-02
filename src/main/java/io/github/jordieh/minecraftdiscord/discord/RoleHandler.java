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

package io.github.jordieh.minecraftdiscord.discord;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.util.ConfigSection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.Optional;
import java.util.UUID;

public class RoleHandler {

    private final Logger logger = LoggerFactory.getLogger(RoleHandler.class);

    private static RoleHandler instance;

    private RoleHandler() {
        logger.debug("Constructing RoleHandler");
    }

    public static RoleHandler getInstance() {
        return instance == null ? instance = new RoleHandler() : instance;
    }

    public boolean isOnlineRoleEnabled() {
        return MinecraftDiscord.getInstance().getConfig().getBoolean(ConfigSection.ROLE_ENABLED);
    }

    public Optional<IRole> getOnlineUserRole() {
        long id = MinecraftDiscord.getInstance().getConfig().getLong(ConfigSection.ROLE_UID);
        return Optional.ofNullable(ClientHandler.getInstance().getGuild().getRoleByID(id));
    }

    public Optional<IRole> useOnlineUserRole() {
        if (isOnlineRoleEnabled()) {
            Optional<IRole> role = getOnlineUserRole();
            if (role.isPresent()) {
                return role;
            }
        }
        return Optional.empty();
    }

    public void giveOnlineRole(UUID uuid) {
        Optional<IRole> role = useOnlineUserRole();
        if (!role.isPresent()) {
            return;
        }

        if (LinkHandler.getInstance().isLinked(uuid)) {
            long id = LinkHandler.getInstance().getLinkedUser(uuid);
            IUser user = ClientHandler.getInstance().getGuild().getUserByID(id);
            if (user == null) {
                LinkHandler.getInstance().unlink(id);
            }
            ClientHandler.getInstance().giveRole(role.get(), user);
        }
    }

    public void removeOnlineRole(UUID uuid) {
        Optional<IRole> role = getOnlineUserRole();
        if (!role.isPresent()) {
            return;
        }

        if (LinkHandler.getInstance().isLinked(uuid)) {
            long id = LinkHandler.getInstance().getLinkedUser(uuid);
            IUser user = ClientHandler.getInstance().getGuild().getUserByID(id);
            if (user == null) {
                LinkHandler.getInstance().unlink(id);
            }
            ClientHandler.getInstance().removeRole(role.get(), user);
        }
    }

    public void giveLinkedUsersOnlineRole() {
        Optional<IRole> role = useOnlineUserRole();
        if (!role.isPresent()) {
            return;
        }

        LinkHandler.getInstance().getLinkMap().forEach((id, uuid) -> {
            IUser user = ClientHandler.getInstance().getGuild().getUserByID(id);
            if (user == null) {
                LinkHandler.getInstance().unlink(id);
                return;
            }

            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                ClientHandler.getInstance().giveRole(role.get(), user);
            }
        });
    }

    public void clearRoleEnabledUsers(boolean checkOnline) {
        Optional<IRole> role = getOnlineUserRole();
        if (!role.isPresent()) {
            return;
        }

        LinkHandler.getInstance().getLinkMap().forEach((id, uuid) -> {
            IUser user = ClientHandler.getInstance().getGuild().getUserByID(id);
            if (user == null) {
                LinkHandler.getInstance().unlink(id);
                return;
            }

            Player player = Bukkit.getPlayer(uuid);
            if (!checkOnline || player == null) {
                ClientHandler.getInstance().removeRole(role.get(), user);
            }
        });
    }
}
