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

import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.discord.LinkHandler;
import io.github.jordieh.minecraftdiscord.discord.RoleHandler;
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

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkCommand extends Translatable implements CommandExecutor {

    @Override
    public void execute(MessageReceivedEvent event, IChannel channel, IMessage message, IUser author, String[] args) {
        LinkHandler linkHandler = LinkHandler.getInstance();

        if (linkHandler.isLinked(author)) { // This gets activated when the user already has a linked account
            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorIcon(author.getAvatarURL());
            builder.withAuthorName(author.getDisplayName(event.getGuild()));
            builder.withColor(0xFF5555);
            builder.withDescription(tr("discord.link.failed", linkHandler.getUserUUIDString(author)));

            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel, builder.build());
            return;
        }

        Matcher matcher = Pattern.compile("\\s*(\\d{6})").matcher(message.getContent());

        if (!matcher.matches()) { // If this activates the code was invalid
            EmbedBuilder builder = new EmbedBuilder();
            builder.withDescription(tr("discord.link.usage"));
            builder.withAuthorIcon(author.getAvatarURL());
            builder.withAuthorName(author.getDisplayName(event.getGuild()));
            builder.withColor(0xFF5555);

            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel, builder.build());
            return;
        }

        int code = Integer.parseInt(matcher.group(1));
        Optional<UUID> optional = linkHandler.linkAccount(author, code);

        if (!optional.isPresent()) { // If this gets triggered, the account linking has failed
            EmbedBuilder builder = new EmbedBuilder();
            builder.withDescription(tr("discord.link.invalid"));
            builder.withAuthorName(author.getDisplayName(event.getGuild()));
            builder.withAuthorIcon(author.getAvatarURL());
            builder.withColor(0xFF5555);

            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel, builder.build());
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(optional.get());

        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorName(offlinePlayer.getName());
        builder.withAuthorIcon(FormatUtil.avatarUrl(optional.get().toString()));
        builder.withColor(author.getColorForGuild(event.getGuild()));
        builder.withDescription(tr("discord.link.success"));

        ClientHandler.getInstance().deleteMessage(message);
        ClientHandler.getInstance().sendMessage(channel, builder.build());

        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            player.sendMessage(tr("discord.link.success.minecraft", FormatUtil.usuableTag(author)));
        }

        RoleHandler.getInstance().giveConnectionRole(optional.get());

    }
}
