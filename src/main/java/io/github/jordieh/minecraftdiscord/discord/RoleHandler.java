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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoleHandler {

    private final Logger logger = LoggerFactory.getLogger(RoleHandler.class);

    private static RoleHandler instance;

    private Map<IRole, Permission> roles; // Permission object to make sure operators don't get all roles
    private final String basePermission;

    private RoleHandler() {
        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();

        this.basePermission = "minecraftdiscord.sync.";
        this.roles = configuration.getLongList("role-synchronization.synchronizable-roles")
                .stream()
                .map(ClientHandler.getInstance().getClient()::getRoleByID)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(r -> r, r -> new Permission(this.basePermission + r.getLongID(), PermissionDefault.FALSE)));

        if (!configuration.getBoolean("connection-role.enabled") && this.roles.isEmpty()) {
            return;
        }

        long delay = configuration.getLong("role-synchronization.synchronization-time", 300); // Default to 300 to prevent everything that could go wrong
        delay = delay * 20; // 20 Minecraft game ticks are equal to 1 second

        MinecraftDiscord.getInstance().getServer().getScheduler()
                .scheduleSyncRepeatingTask(MinecraftDiscord.getInstance(), new RoleRunnable(), delay, delay);
    }

    public static RoleHandler getInstance() {
        return instance == null ? instance = new RoleHandler() : instance;
    }

    public boolean useConnectionRole() {
        return MinecraftDiscord.getInstance().getConfig().getBoolean("connection-role.enabled");
    }

    public Optional<IRole> getConnectionRole() {
        long id = MinecraftDiscord.getInstance().getConfig().getLong("connection-role.unique");
        return Optional.ofNullable(ClientHandler.getInstance().getClient().getRoleByID(id));
    }

    /**
     * Gives the Discord user the connection role when the following conditions are met:
     *  - The connection role is enabled in the config
     *  - The specified role in the config is valid
     *  - The Discord user is linked to the specified UUID
     * @param uuid The UUID of a player
     * @return true if the role has been given, in all other cases false
     */
    public boolean giveConnectionRole(UUID uuid) {
        if (!this.useConnectionRole()) {
            return false;
        }

        Optional<IRole> optional = this.getConnectionRole();
        if (!optional.isPresent()) {
            return false;
        }

        Optional<Map.Entry<Long, UUID>> entryOptional = LinkHandler.getInstance().getLinkedUser(uuid);

        if (!entryOptional.isPresent()) {
            return false;
        }

        long id = entryOptional.get().getKey();

        IUser user = ClientHandler.getInstance().getClient().getUserByID(id);

        if (user == null) {
            LinkHandler.getInstance().unlink(id);
            return false;
        }

        ClientHandler.getInstance().giveRole(optional.get(), user);
        return true;
    }

    /**
     * Gives the Discord user the connection role when the following conditions are met:
     *  - The specified role in the config is valid
     *  - The Discord user is linked to the specified UUID
     * @param uuid The UUID of a player
     * @return true if the role has been given, in all other cases false
     */
    public boolean removeConnectionRole(UUID uuid) {
        Optional<IRole> optional = this.getConnectionRole();
        if (!optional.isPresent()) {
            return false;
        }

        Optional<Map.Entry<Long, UUID>> entryOptional = LinkHandler.getInstance().getLinkedUser(uuid);

        if (!entryOptional.isPresent()) {
            return false;
        }

        long id = entryOptional.get().getKey();

        IUser user = ClientHandler.getInstance().getClient().getUserByID(id);

        if (user == null) {
            LinkHandler.getInstance().unlink(id);
            return false;
        }

        ClientHandler.getInstance().removeRole(optional.get(), user);
        return true;
    }

    /**
     * Gives the connection role specified in the config.yml to all online users when the following conditions are met:
     *  - The connection-role.enabled option is enabled in the config
     *  - The specified role in the config is valid
     * @return true if the roles have been distributed, false otherwise
     */
    public boolean distributeConnectionRole() {
        if (!this.useConnectionRole()) {
            return false;
        }

        Optional<IRole> optional = this.getConnectionRole();
        if (!optional.isPresent()) {
            return false;
        }

        // TODO Switch this functionality around to use Bukkit#getOnlinePlayers();
        LinkHandler.getInstance().getLinkMap().forEach((id, uuid) -> {
            IUser user = ClientHandler.getInstance().getClient().getUserByID(id);
            if (user == null) {
                LinkHandler.getInstance().unlink(id);
                return;
            }

            if (Bukkit.getPlayer(uuid) != null) { // Check if the Player is online
                ClientHandler.getInstance().giveRole(optional.get(), user);
            }

        });

        return true;
    }

    /**
     * Clears the connection role only when the role specified in the config.yml is valid
     * @param checkOnline Check if the connected player is online before removing the role
     * @return true when an attempt to remove the roles has succeeded, false otherwise
     */
    public boolean clearConnectionUsers(boolean checkOnline) {
        Optional<IRole> optional = this.getConnectionRole();
        if (!optional.isPresent()) {
            return false;
        }

        // TODO Switch this functionality around to use Bukkit#getOnlinePlayers();
        LinkHandler.getInstance().getLinkMap().forEach((id, uuid) -> {
            IUser user = ClientHandler.getInstance().getClient().getUserByID(id);
            if (user == null) {
                LinkHandler.getInstance().unlink(id);
                return;
            }

            if (!checkOnline || Bukkit.getPlayer(uuid) != null) {
                ClientHandler.getInstance().removeRole(optional.get(), user);
            }

        });

        return true;
    }

    private final class RoleRunnable implements Runnable {
        @Override
        public void run() {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(p -> LinkHandler.getInstance().isLinked( p.getUniqueId()))
                    .collect(Collectors.toList());

            for (Player player : players) {
                Optional<Map.Entry<Long, UUID>> optional = LinkHandler.getInstance().getLinkedUser(player.getUniqueId());
                if (!optional.isPresent()) {
                    return;
                }

                Map.Entry<Long, UUID> entry = optional.get();
                IUser user = ClientHandler.getInstance().getClient().getUserByID(entry.getKey());
                if (user == null) {
                    return;
                }

                roles.forEach((role, permission) -> {
                    if (player.hasPermission(permission)) {
                        if (!user.hasRole(role)) {
                            ClientHandler.getInstance().giveRole(role, user);
                        }
                    } else {
                        if (user.hasRole(role)) {
                            ClientHandler.getInstance().removeRole(role, user);
                        }
                    }
                });
            }

        }
    }
}
