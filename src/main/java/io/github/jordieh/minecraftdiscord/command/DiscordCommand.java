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

package io.github.jordieh.minecraftdiscord.command;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.jordieh.minecraftdiscord.util.LangUtil.tr;

public class DiscordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String invite = MinecraftDiscord.getInstance().getConfig().getString("invite");

        if (invite.equals("0")) {
            sender.sendMessage(tr("command.discord.invite"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("https://discord.gg/" + invite);
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("minecraftdiscord.discord")) {
            player.sendMessage(tr("command.discord.nopermission"));
            return true;
        }

        String message = "tellraw " + player.getName() + " " + tr("command.discord.message", invite);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message);
        return true;
    }
}
