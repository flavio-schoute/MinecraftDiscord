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

package io.github.jordieh.minecraftdiscord.discord;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import sx.blah.discord.handle.obj.IWebhook;

import java.util.concurrent.TimeUnit;

@Deprecated
public class WebhookHandler implements Listener {

    private static WebhookHandler instance;

    private JavaPlugin javaPlugin = MinecraftDiscord.getInstance();

    private int rateLimitRemaining;
    private long rateLimitReset;
    private int requestCount;
    private long startTime = System.currentTimeMillis();
    private int rateLimit;

    @Deprecated
    public static WebhookHandler getInstance() {
        return instance == null ? instance = new WebhookHandler() : instance;
    }

    @Deprecated
    public IWebhook getWebhook(World world) {
        String target = "MinecraftDiscord [" + world.getName() + "]";
        return ClientHandler.getInstance().getClient().getGuildByID(429002537673293835L).getWebhooks().stream()
                .filter(webhook -> webhook.getDefaultName().equals(target))
                .findFirst().orElseGet(() -> createWebhook(world));
    }

    @Deprecated
    private IWebhook createWebhook(World world) {
        String name = "MinecraftDiscord [" + world.getName() + "]";
        return ClientHandler.getInstance().getClient().getGuildByID(429002537673293835L).getChannelsByName(world.getName()).get(0).createWebhook(name);
    }

    public void sendWebhook(IWebhook webhook, String username, String content, String avatarUrl) {
        String url = "https://discordapp.com/api/webhooks/" + webhook.getLongID() + "/" + webhook.getToken();

        Runnable runnable = () -> {
            HttpResponse<String> response;
            try {
                response = Unirest.post(url)
                        .field("content", content)
                        .field("username", username)
                        .field("avatar_url", avatarUrl)
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
                return;
            }

            this.rateLimitRemaining = Integer.parseInt(response.getHeaders().getFirst("X-RateLimit-Remaining"));
            this.rateLimitReset = Long.parseLong(response.getHeaders().getFirst("X-RateLimit-Reset"));
            this.rateLimit = Integer.parseInt(response.getHeaders().getFirst("X-RateLimit-Limit"));

            System.out.println("Received API response: " + response.getStatus() + (response.getBody() == null ? "" : " ->\n" + response.getBody()));
        };

        this.schedule(runnable);

    }

    private void schedule(Runnable runnable) {
        requestCount++;
        if (requestCount == rateLimitRemaining) {
            long diff = System.currentTimeMillis() - startTime;

            if (diff < 1000) {
                javaPlugin.getServer().getScheduler().runTaskLater(javaPlugin, runnable, (TimeUnit.MILLISECONDS.toSeconds(diff) + rateLimitReset) * 20L);
            }

            startTime = System.currentTimeMillis();
            requestCount = 0;
        }
        javaPlugin.getServer().getScheduler().runTask(javaPlugin, runnable);
    }

}
