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
import io.github.jordieh.minecraftdiscord.discord.LinkHandler;
import io.github.jordieh.minecraftdiscord.util.ConfigSection;
import io.github.jordieh.minecraftdiscord.util.EmbedUtil;
import io.github.jordieh.minecraftdiscord.util.FormatUtil;
import org.bukkit.Bukkit;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import static io.github.jordieh.minecraftdiscord.util.LangUtil.tr;

public class MessageReceivedEventHandler implements IListener<MessageReceivedEvent> {

    @Override
    public void handle(MessageReceivedEvent event) {
        IChannel channel = event.getChannel();
        IMessage message = event.getMessage();
        IUser author = event.getAuthor();

        if (author.isBot() || message.getWebhookLongID() != 0) {
            return;
        }

        if (LinkHandler.getInstance().handleLinking(event)) {
            return;
        }

        if (MinecraftDiscord.getInstance().getConfig().getBoolean(ConfigSection.LINKING_ENABLED)) {
            if (!LinkHandler.getInstance().isLinked(author.getLongID())) {
                return;
            }
        }

        String messageA = FormatUtil.stripColors(event.getMessage().getContent());
        int maxLength = (messageA.length() < 257 )? messageA.length() : 257;
        if (maxLength == 257) {
            message.reply(tr("discord.message.length"),
                    EmbedUtil.createEmbed(messageA));
        }
        messageA = messageA.substring(0, maxLength);
        String format = MinecraftDiscord.getInstance().getConfig().getString(ConfigSection.DISCORD_FORMAT)
                .replace("#USER", FormatUtil.stripColors(author.getDisplayName(event.getGuild())))
                .replace("#MESSAGE", messageA)
                .replace("#CHANNEL", channel.getName());
        Bukkit.broadcastMessage(FormatUtil.formatColors(format));

    }
}
