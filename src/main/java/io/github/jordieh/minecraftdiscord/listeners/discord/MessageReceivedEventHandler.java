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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.UUID;

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
                String desc = ":tickets: Invalid usage: Please use `/link <code>`";
                ClientHandler.getInstance().deleteMessage(message);
                ClientHandler.getInstance().sendMessage(channel, EmbedUtil.createEmbed(desc, 0xFF5555));
                return;
            }
            int code = Integer.parseInt(content.replaceAll("/link\\s+", ""));
            if (LinkHandler.getInstance().linkAccount(author, code)) {
                String desc = ":1234: Invalid code, please execute the `/link` command ingame!";
                ClientHandler.getInstance().deleteMessage(message);
                ClientHandler.getInstance().sendMessage(channel, EmbedUtil.createEmbed(desc, 0xFF5555));
                return;
            }

            UUID uuid = LinkHandler.getInstance().getLinkMap().get(author.getLongID());
            String url = MinecraftDiscord.getInstance().getConfig().getString(ConfigSection.RENDER_LINK)
                    .replace("#uuid", uuid.toString());
            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel,
                    EmbedUtil.createEmbed("Successfully linked your Minecraft account to Discord",
                            author.getColorForGuild(message.getGuild()), uuid.toString(), url));
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                String format = "%sYou have successfully linked your Minecraft account with Discord! (%s%s%s)";
                player.sendMessage(String.format(format, ChatColor.BLUE, ChatColor.AQUA,
                        (author.getName() + author.getDiscriminator()), ChatColor.BLUE));
            }
            return;
        }

        if (!WorldHandler.getInstance().getLongMap().containsValue(channel.getLongID())) {
            return; //@TODO Handle console
        }

        String messageA = FormatUtil.stripColors(event.getMessage().getContent());
        int maxLength = (messageA.length() < 257 )? messageA.length() : 257;
        if (maxLength == 257) {
            message.reply("Your message was to long, it has been converted to the following:",
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
