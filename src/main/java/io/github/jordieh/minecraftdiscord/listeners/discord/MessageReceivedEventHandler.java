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
import io.github.jordieh.minecraftdiscord.discord.LinkHandler;
import io.github.jordieh.minecraftdiscord.util.ConfigSection;
import io.github.jordieh.minecraftdiscord.util.EmbedUtil;
import io.github.jordieh.minecraftdiscord.util.FormatUtil;
import io.github.jordieh.minecraftdiscord.world.WorldHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.UUID;

import static io.github.jordieh.minecraftdiscord.util.LangUtil.tr;

public class MessageReceivedEventHandler implements IListener<MessageReceivedEvent> {

    @Override
    public void handle(MessageReceivedEvent event) {
        IChannel channel = event.getChannel();
        IUser author = event.getAuthor();
        IMessage message = event.getMessage();
        String content = message.getContent();

        if (author.isBot() || message.getWebhookLongID() != 0) {
            return;
        }

        if (content.startsWith("/link")) {
            if (!content.matches("/link\\s*\\d{6}")) {
                String desc = tr("discord.link.usage");
                ClientHandler.getInstance().deleteMessage(message);
                ClientHandler.getInstance().sendMessage(channel, EmbedUtil.createEmbed(desc, 0xFF5555));
                return;
            }
            int code = Integer.parseInt(content.replaceAll("/link\\s+", ""));
            if (LinkHandler.getInstance().linkAccount(author, code)) {
                String desc = tr("discord.link.invalid");
                ClientHandler.getInstance().deleteMessage(message);
                ClientHandler.getInstance().sendMessage(channel, EmbedUtil.createEmbed(desc, 0xFF5555));
                return;
            }

            UUID uuid = LinkHandler.getInstance().getLinkMap().get(author.getLongID());
            OfflinePlayer temp = Bukkit.getOfflinePlayer(uuid);
            String url = MinecraftDiscord.getInstance().getConfig().getString(ConfigSection.RENDER_LINK)
                    .replace("#uuid", uuid.toString());
            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel,
                    EmbedUtil.createEmbed(tr("discord.link.success"),
                            author.getColorForGuild(message.getGuild()), tr("discord.link.success.title",
                                    uuid.toString(), temp.getName()), url));
            Player player = temp.getPlayer();
            if (player != null) {
                player.sendMessage(tr("discord.link.success.minecraft",
                        (author.getName() + "#" + author.getDiscriminator())));
            }
            return;
        }

        if (!WorldHandler.getInstance().getLongMap().containsValue(channel.getLongID())) {
            return; //@TODO Handle console
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
