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

package io.github.jordieh.minecraftdiscord.listeners.discord;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.discord.CommandHandler;
import io.github.jordieh.minecraftdiscord.discord.LinkHandler;
import io.github.jordieh.minecraftdiscord.util.FormatUtil;
import io.github.jordieh.minecraftdiscord.util.Translatable;
import io.github.jordieh.minecraftdiscord.world.ChannelHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class MessageReceivedEventHandler extends Translatable implements IListener<MessageReceivedEvent> {

    @Override
    public void handle(MessageReceivedEvent event) {
        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();
        IChannel channel = event.getChannel();
        IMessage message = event.getMessage();
        IUser author = event.getAuthor();

        if (configuration.getBoolean("options.disable-automated-users")) {
            if (author.isBot() || message.getWebhookLongID() != 0) {
                return;
            }
        }

        if (CommandHandler.getInstance().handleExecution(event)) {
            return;
        }
        if (!ChannelHandler.getInstance().getLongMap().containsValue(channel.getLongID())) {
            return;
        }

        if (MinecraftDiscord.getInstance().getConfig().getBoolean("options.require-linking")) {
            if (!LinkHandler.getInstance().isLinked(author)) {
                return;
            }
        }

        int truncationLength = configuration.getInt("options.truncation-size");
        String content = FormatUtil.formatRegex(message.getContent());

        if (content.length() > truncationLength) {
            content = FormatUtil.truncateString(content, truncationLength);

            EmbedBuilder builder = new EmbedBuilder();
            builder.withDescription("Your message has been changed to the following```" + content + "```");
            builder.withTitle("You have overridden the character limit of " + truncationLength + " characters");
            builder.withAuthorIcon(author.getAvatarURL());
            builder.withColor(author.getColorForGuild(event.getGuild()));
            builder.withAuthorName(author.getName());
            ClientHandler.getInstance().sendMessage(channel, builder.build());
        }

        String msg = tr("discord.message.format",
                channel.getName(),
                FormatUtil.stripColors(author.getName()),
                content);

        Bukkit.broadcastMessage(FormatUtil.formatColors(msg));
    }
}
