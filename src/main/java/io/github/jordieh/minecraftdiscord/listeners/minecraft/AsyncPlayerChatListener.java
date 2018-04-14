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
import io.github.jordieh.minecraftdiscord.util.FormatUtil;
import io.github.jordieh.minecraftdiscord.util.MessageType;
import io.github.jordieh.minecraftdiscord.world.ChannelHandler;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Optional;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();

        MessageType messageType;
        try {
            messageType = MessageType.valueOf(configuration.getString("options.message-type").toUpperCase());
            if (messageType == MessageType.WEBHOOK) {
                messageType = MessageType.EMBED_ADVANCED;
            }
        } catch (IllegalArgumentException e) {
            messageType = MessageType.MESSAGE;
        }

        Player player = event.getPlayer();
        World world = player.getWorld();

        Optional<IChannel> specific = ChannelHandler.getInstance().getConnectedChannel(world.getName());
        Optional<IChannel> global = ChannelHandler.getInstance().getGlobalChannel();

        switch (messageType) {
            case MESSAGE: {
                specific.ifPresent(channel ->
                    ClientHandler.getInstance().sendMessage(channel, String.format("%s: %s", player.getName(), event.getMessage())));
                global.ifPresent(channel ->
                    ClientHandler.getInstance().sendMessage(channel, String.format("%s: %s", player.getName(), event.getMessage())));
                break;
            }
            case EMBED: {
                EmbedBuilder builder = new EmbedBuilder();
                builder.withDescription(event.getMessage());
                builder.withAuthorName(player.getName());
                builder.withAuthorIcon(FormatUtil.avatarUrl(player.getUniqueId().toString()));

                specific.ifPresent(channel ->
                        ClientHandler.getInstance().sendMessage(channel, builder.build()));
                global.ifPresent(channel ->
                        ClientHandler.getInstance().sendMessage(channel, builder.build()));
                break;
            }
            case EMBED_ADVANCED: {
                EmbedBuilder builder = new EmbedBuilder();
                builder.withDescription(event.getMessage());
                builder.withAuthorName(player.getName());
                builder.withThumbnail(FormatUtil.avatarUrl(player.getUniqueId().toString()));

                specific.ifPresent(channel ->
                    ClientHandler.getInstance().sendMessage(channel, builder.build()));
                global.ifPresent(channel ->
                    ClientHandler.getInstance().sendMessage(channel, builder.build()));
                break;
            }
//            case WEBHOOK: {
//                String avatarUrl = plugin.getConfig().getString("options.message-render")
//                        .replace("<uuid>", player.getUniqueId().toString());
//
//                IWebhook webhook = WebhookHandler.getInstance().getWebhook(player.getWorld());
//                WebhookHandler.getInstance().sendWebhook(
//                        webhook,
//                        player.getName(),
//                        event.getMessage(),
//                        avatarUrl
//                );
//                break;
//            }
            default: {
                // Should never technically happen
                break;
            }
        }
    }
}
