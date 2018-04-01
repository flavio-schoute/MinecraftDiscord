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

package io.github.jordieh.minecraftdiscord.discord;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bukkit.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IWebhook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebhookHandler {

    private final Logger logger = LoggerFactory.getLogger(WebhookHandler.class);

    private static WebhookHandler instance;

    private WebhookHandler() {
    }

    public static WebhookHandler getInstance() {
        return instance == null ? instance = new WebhookHandler() : instance;
    }

    public IWebhook getWebhook(World world) {
        logger.trace("Attempting to get a webhook for {}", world.getName());
        String target = "MinecraftDiscord [" + world.getName() + "]";
        return ClientHandler.getInstance().getGuild().getWebhooks().stream()
                .filter(webhook -> webhook.getDefaultName().equals(target))
                .findFirst().orElseGet(() -> createWebhook(world));
    }

    private IWebhook createWebhook(World world) {
        logger.trace("Attempting to create a webhook for {}", world.getName());
        String name = "MinecraftDiscord [" + world.getName() + "]";
        return ClientHandler.getInstance().getGuild().getChannelsByName(world.getName()).get(0).createWebhook(name);
    }

    public void sendWebhook(IWebhook webhook, String username, String content, String avatarUrl) {
        String uri = "https://discordapp.com/api/webhooks/" + webhook.getLongID() + "/" + webhook.getToken();
        logger.trace("Trying to open POST request to {}", "https://discordapp.com/api/webhooks/");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(uri);
            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("content", content));
            pairs.add(new BasicNameValuePair("username", username));
            pairs.add(new BasicNameValuePair("avatar_url", avatarUrl));
            httpPost.setEntity(new UrlEncodedFormEntity(pairs));
            logger.trace("Trying to execute post request [{}] [{}] [{}]", username, content, avatarUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int responseCode = response.getStatusLine().getStatusCode();
                if (!(responseCode == 200 || responseCode == 204)) {
                    HttpEntity httpEntity = response.getEntity();
                    String body = EntityUtils.toString(httpEntity);
                    logger.warn("Response code of POST request was unsuccessful: {}", body);
                    logger.trace("Trying to consume HttpEntity {}", httpEntity.toString());
                    EntityUtils.consume(httpEntity);
                }
            }
        } catch (IOException e) {
            logger.warn("Error while sending POST request via WebhookHandler#sendWebhook();", e);
        }
    }
}
