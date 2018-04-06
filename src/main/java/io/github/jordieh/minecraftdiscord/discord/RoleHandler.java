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
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RoleHandler {

    private final Logger logger = LoggerFactory.getLogger(RoleHandler.class);

    private static RoleHandler instance;

    private Set<IRole> roles;

    private RoleHandler() {
        logger.debug("Constructing RoleHandler");
        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();

        roles = configuration.getLongList("role-synchronization.synchronizable-roles")
                .stream()
                .filter(aLong -> aLong != 0)
                .map(ClientHandler.getInstance().getClient()::getRoleByID)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!configuration.getBoolean("connection-role.enabled") && roles.isEmpty()) {
            return;
        }

        long delay = configuration.getLong("role-synchronization.synchronization-time");
        delay = TimeUnit.SECONDS.toMillis(delay);

        MinecraftDiscord.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(
                MinecraftDiscord.getInstance(), () -> Bukkit.getOnlinePlayers().stream()
                        .filter(p -> LinkHandler.getInstance().isLinked((p.getUniqueId())))
                        .forEach(player -> roles.forEach(role -> {
                            if (player.hasPermission("minecraftdiscord.sync." + role.getLongID())) {
                                long userID = LinkHandler.getInstance().getLinkedUser(player.getUniqueId());
                                IUser user = ClientHandler.getInstance().getClient().getUserByID(userID);
                                if (user != null) {
                                    ClientHandler.getInstance().giveRole(role, user);
                                }
                            }
                        })), delay, delay);
    }

    public static RoleHandler getInstance() {
        return instance == null ? instance = new RoleHandler() : instance;
    }

    public boolean isOnlineRoleEnabled() {
        return MinecraftDiscord.getInstance().getConfig().getBoolean("connection-role.enabled");
    }

    public Optional<IRole> getOnlineUserRole() {
        long id = MinecraftDiscord.getInstance().getConfig().getLong("connection-role.unique");
        return Optional.ofNullable(ClientHandler.getInstance().getClient().getRoleByID(id));
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
            IUser user = ClientHandler.getInstance().getClient().getUserByID(id);
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
            IUser user = ClientHandler.getInstance().getClient().getUserByID(id);
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
            IUser user = ClientHandler.getInstance().getClient().getUserByID(id);
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
            IUser user = ClientHandler.getInstance().getClient().getUserByID(id);
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
