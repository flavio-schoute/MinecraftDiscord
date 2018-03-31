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

package io.github.jordieh.minecraftdiscord.listeners;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.discord.WebhookHandler;
import io.github.jordieh.minecraftdiscord.util.ConfigSection;
import io.github.jordieh.minecraftdiscord.util.EventListener;
import io.github.jordieh.minecraftdiscord.util.MessageType;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IWebhook;
import sx.blah.discord.util.EmbedBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AsyncPlayerChatListener extends EventListener<AsyncPlayerChatEvent> {

    public AsyncPlayerChatListener() {
        super(event -> {
            Plugin plugin = MinecraftDiscord.getInstance();
            FileConfiguration configuration = plugin.getConfig();
            MessageType messageType;
            try {
                messageType = MessageType.valueOf(configuration.getString(ConfigSection.OUTPUT_TYPE.PATH).toUpperCase());
            } catch (IllegalArgumentException e) {
                messageType = MessageType.MESSAGE;
            }

            // TODO Channels per world
            Player player = event.getPlayer();
            List<IChannel> channels = ClientHandler.getInstance().getGuild().getChannelsByName(player.getWorld().getName());
            if (channels.isEmpty()) {
                return;
            }
            IChannel channel = channels.get(0);

            switch (messageType) {
                case MESSAGE: {
                    ClientHandler.getInstance().sendMessage(channel, String.format("%s: %s", player.getName(), event.getMessage()));
                    break;
                }
                case EMBED: {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.withDescription(event.getMessage());
                    builder.withAuthorName(player.getName());
                    builder.withAuthorIcon("https://mc-heads.net/avatar/" + player.getUniqueId() + "/128");
                    ClientHandler.getInstance().sendMessage(channel, builder.build());
                    break;
                }
                case WEBHOOK: {
                    // TODO Improve code execution
                    IWebhook webhook = WebhookHandler.getWebhook(player.getWorld());
                    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                        HttpPost httpPost = new HttpPost(String.format("https://canary.discordapp.com/api/webhooks/%s/%s", webhook.getLongID(), webhook.getToken()));

                        List <NameValuePair> nvps = new ArrayList<>();
                        nvps.add(new BasicNameValuePair("content", event.getMessage()));
                        nvps.add(new BasicNameValuePair("username", player.getName()));
                        nvps.add(new BasicNameValuePair("avatar_url", "https://mc-heads.net/avatar/" + player.getUniqueId() + "/128"));

                        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

                        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                            HttpEntity entity = response.getEntity();
                            EntityUtils.consume(entity);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default: {
                    // Should never technically happen
                    break;
                }
            }
        });
    }
}
