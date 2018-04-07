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

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class InfoCommand implements CommandExecutor {

    @Override
    public void execute(MessageReceivedEvent event, IChannel channel, IMessage message, IUser author, String[] args) {
        if (!channel.getName().equalsIgnoreCase("minecraftdiscord")) { // Restrict the channels in which this can execute
            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(0x5599cc);
            builder.withDescription("This command can only be executed in a channel called `minecraftdiscord`");
            builder.withAuthorName(author.getName());
            builder.withAuthorIcon(author.getAvatarURL());

            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel, builder.build());
            return;
        }

        StringBuilder channelBuilder = new StringBuilder();
        event.getGuild().getChannels().forEach(c -> {
            channelBuilder.append("~ `");
            channelBuilder.append(c.getLongID());
            channelBuilder.append("` = `#");
            channelBuilder.append(c.getName());
            channelBuilder.append("`\n");
        });

        StringBuilder roleBuilder = new StringBuilder();
        event.getGuild().getRoles().forEach(r -> {
            roleBuilder.append("~ `");
            roleBuilder.append(r.getLongID());
            roleBuilder.append("` = `@");
            roleBuilder.append(r.getName());
            roleBuilder.append("`\n");
        });

        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorName(author.getName());
        builder.withAuthorIcon(author.getAvatarURL());
        builder.withTitle("Guild information for " + event.getGuild().getName());
        builder.withColor(0x5599cc);
        builder.appendField("Guild ID", event.getGuild().getStringID(), true);
        builder.appendField("Plugin Version", MinecraftDiscord.getInstance().getDescription().getVersion(), true);
        builder.appendField("Connected channels", channelBuilder.toString(), true);
        builder.appendField("Available roles", roleBuilder.toString(), true);

        ClientHandler.getInstance().deleteMessage(message);
        ClientHandler.getInstance().sendMessage(channel, builder.build());
    }
}
