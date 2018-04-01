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

import io.github.jordieh.minecraftdiscord.discord.LinkHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LinkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can link their Discord account");
            return true;
        }

        if (!sender.hasPermission("minecraftdiscord.connect")) {
            sender.sendMessage(ChatColor.BLUE + "You currently are not allowed to link your account");
            return true;
        }

        int code = LinkHandler.getInstance().generateCode(((Player) sender).getUniqueId());
        String format = "%sYour authentication code is %s%s%s, you can link your account by typing '/link' in Discord";
        sender.sendMessage(String.format(format, ChatColor.BLUE, ChatColor.AQUA, code, ChatColor.BLUE, code));
        return true;
    }
}
