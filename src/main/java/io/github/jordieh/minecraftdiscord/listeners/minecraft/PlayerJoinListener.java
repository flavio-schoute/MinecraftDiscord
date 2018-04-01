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

package io.github.jordieh.minecraftdiscord.listeners.minecraft;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.discord.LinkHandler;
import io.github.jordieh.minecraftdiscord.util.ConfigSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Plugin plugin = MinecraftDiscord.getInstance();
        FileConfiguration configuration = plugin.getConfig();

        System.out.println(" >>>>>>>>>>>>>>] Join");
        if (!configuration.getBoolean(ConfigSection.ROLE_ENABLED)) {
            System.out.println("Online player roles are disabled");
            return;
        }

        UUID uuid = event.getPlayer().getUniqueId();
        if (!LinkHandler.getInstance().isLinked(uuid)) {
            System.out.println("Player " + uuid.toString() + " is not linked");
            return;
        }

        IRole role = ClientHandler.getInstance().getGuild().getRoleByID(configuration.getLong(ConfigSection.ROLE_UID));
        if (role ==null) {
            System.out.println("Cannot find role with id " + configuration.getLong(ConfigSection.ROLE_UID));
            return;
        }

        long temp = LinkHandler.getInstance().getLinkedUser(uuid);
        IUser user = ClientHandler.getInstance().getGuild().getUserByID(temp);
        if (user == null) {
            System.out.println("Cannot find user with id " + temp);
            return;
        }

        ClientHandler.getInstance().giveRole(role, user);
    }


}
