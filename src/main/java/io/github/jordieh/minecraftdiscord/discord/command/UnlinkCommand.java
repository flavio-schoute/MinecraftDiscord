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

package io.github.jordieh.minecraftdiscord.discord.command;

import io.github.jordieh.minecraftdiscord.api.ConnectionRoute;
import io.github.jordieh.minecraftdiscord.api.events.PlayerAccountUnLinkEvent;
import io.github.jordieh.minecraftdiscord.common.UserPair;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.discord.LinkHandler;
import io.github.jordieh.minecraftdiscord.util.FormatUtil;
import io.github.jordieh.minecraftdiscord.util.Translatable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class UnlinkCommand extends Translatable implements CommandExecutor {

    @Override
    public void execute(MessageReceivedEvent event, IChannel channel, IMessage message, IUser author, String[] args) {
        LinkHandler linkHandler = LinkHandler.getInstance();
        UserPair pair = linkHandler.unlink(author.getLongID());

        if (!pair.isEmpty()) {
            Bukkit.getPluginManager().callEvent(new PlayerAccountUnLinkEvent(pair.getRight(), author, ConnectionRoute.DISCORD));

            String s = pair.getRight().toString();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(pair.getRight());

            EmbedBuilder builder = new EmbedBuilder();
            builder.withDescription(tr("discord.unlink.success"));
            builder.withAuthorIcon(FormatUtil.avatarUrl(s));
            builder.withAuthorName(offlinePlayer.getName());
            builder.withColor(author.getColorForGuild(event.getGuild()));

            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel, builder.build());

            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                player.sendMessage(tr("discord.unlink.minecraft", FormatUtil.usuableTag(author)));
            }
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withDescription(tr("discord.unlink.failed"));
            builder.withColor(0xFF5555);

            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel, builder.build());
        }
    }
}
