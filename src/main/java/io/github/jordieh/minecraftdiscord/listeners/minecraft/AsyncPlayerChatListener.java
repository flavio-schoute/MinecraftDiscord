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
import io.github.jordieh.minecraftdiscord.discord.WebhookHandler;
import io.github.jordieh.minecraftdiscord.util.ConfigSection;
import io.github.jordieh.minecraftdiscord.util.MessageType;
import io.github.jordieh.minecraftdiscord.world.WorldHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IWebhook;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Optional;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Plugin plugin = MinecraftDiscord.getInstance();
        FileConfiguration configuration = plugin.getConfig();
        MessageType messageType;
        try {
            messageType = MessageType.valueOf(configuration.getString(ConfigSection.OUTPUT_TYPE).toUpperCase());
        } catch (IllegalArgumentException e) {
            messageType = MessageType.MESSAGE;
        }

        // TODO Channels per world
        Player player = event.getPlayer();
        if (!WorldHandler.getInstance().getLongMap().containsKey(event.getPlayer().getWorld().getName())) {
            return;
        }

        Optional<IChannel> channelOptional = WorldHandler.getInstance().getWorldChannel(player.getWorld());
        if (!channelOptional.isPresent()) return;

        IChannel channel = channelOptional.get();

        switch (messageType) {
            case MESSAGE: {
                ClientHandler.getInstance().sendMessage(channel, String.format("%s: %s", player.getName(), event.getMessage()));
                break;
            }
            case EMBED: {
                String avatarUrl = plugin.getConfig().getString(ConfigSection.RENDER_LINK)
                        .replace("#uuid", player.getUniqueId().toString());

                EmbedBuilder builder = new EmbedBuilder();
                builder.withDescription(event.getMessage());
                builder.withAuthorName(player.getName());
                builder.withAuthorIcon(avatarUrl);
                ClientHandler.getInstance().sendMessage(channel, builder.build());
                break;
            }
            case WEBHOOK: {
                String avatarUrl = plugin.getConfig().getString(ConfigSection.RENDER_LINK)
                        .replace("#uuid", player.getUniqueId().toString());

                IWebhook webhook = WebhookHandler.getInstance().getWebhook(player.getWorld());
                WebhookHandler.getInstance().sendWebhook(
                        webhook,
                        player.getName(),
                        event.getMessage(),
                        avatarUrl
                );
                break;
            }
            default: {
                // Should never technically happen
                break;
            }
        }
    }
}
