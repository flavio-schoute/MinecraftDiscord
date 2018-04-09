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

package io.github.jordieh.minecraftdiscord.world;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IWebhook;

import java.util.concurrent.TimeUnit;

@Deprecated
public class WebhookHandler implements Listener {

    private final Logger logger = LoggerFactory.getLogger(WebhookHandler.class);

    private static WebhookHandler instance;

    private JavaPlugin javaPlugin = MinecraftDiscord.getInstance();

    private int rateLimitRemaining;
    private long rateLimitReset;
    private int requestCount;
    private long startTime;
    private int rateLimit;

    public static WebhookHandler getInstance() {
        return instance == null ? instance = new WebhookHandler() : instance;
    }

    public IWebhook getWebhook(World world) {
        logger.trace("Attempting to get a webhook for {}", world.getName());
        String target = "MinecraftDiscord [" + world.getName() + "]";
        return ClientHandler.getInstance().getClient().getGuildByID(429002537673293835L).getWebhooks().stream()
                .filter(webhook -> webhook.getDefaultName().equals(target))
                .findFirst().orElseGet(() -> createWebhook(world));
    }

    private IWebhook createWebhook(World world) {
        logger.trace("Attempting to create a webhook for {}", world.getName());
        String name = "MinecraftDiscord [" + world.getName() + "]";
        return ClientHandler.getInstance().getClient().getGuildByID(429002537673293835L).getChannelsByName(world.getName()).get(0).createWebhook(name);
    }

//    public void sendWebhook(IWebhook webhook, String username, String content, String avatarUrl) {
//        String url = "https://discordapp.com/api/webhooks/" + webhook.getLongID() + "/" + webhook.getToken();
//
//        Runnable runnable = () -> {
//            HttpResponse<String> response;
//            try {
//                response = Unirest.post(url)
//                        .field("content", content)
//                        .field("username", username)
//                        .field("avatar_url", avatarUrl)
//                        .asString();
//            } catch (UnirestException e) {
//                e.printStackTrace();
//                return;
//            }
//
//            this.rateLimitRemaining = Integer.parseInt(response.getHeaders().getFirst("X-RateLimit-Remaining"));
//            this.rateLimitReset = Long.parseLong(response.getHeaders().getFirst("X-RateLimit-Reset"));
//            this.rateLimit = Integer.parseInt(response.getHeaders().getFirst("X-RateLimit-Limit"));
//
//            System.out.println("Received API response: " + response.getStatus() + (response.getBody() == null ? "" : " ->\n" + response.getBody()));
//
//            int $1 = this.rateLimitRemaining;
//            long $2 = this.rateLimitReset;
//            int $3 = this.rateLimit;
//
//            if (response.getStatus() == 429) {
//                System.out.println("\n\n\t<------------------->" +
//                        "\t\nrateLimitRemaining= " + $1 +
//                        "\t\nrateLimitReset= " + $2 +
//                        "\t\nrateLimit= " + $3 +
//                        "\n\t<------------------->\n");
//            }
//        };
//
//        this.schedule(runnable);
//
//    }
//
//    private void schedule(Runnable runnable) {
//        requestCount++;
//        if (requestCount == rateLimit) {
//            long diff = System.currentTimeMillis() - startTime;
//
//            if (diff < 1000) {
//                javaPlugin.getServer().getScheduler().runTaskLater(javaPlugin, runnable, TimeUnit.MILLISECONDS.toSeconds(diff) * 20);
//            }
//
//            startTime = System.currentTimeMillis();
//            requestCount = 0;
//        }
//    }

    private void register(Runnable runnable) {
        long reset = this.rateLimitReset * 1000;
        long diff = reset - System.currentTimeMillis();

        if (diff >= 0) {
//            this.runnableQueue.add(new Pair<>(reset, runnable));
            MinecraftDiscord.getInstance().getServer().getScheduler().runTaskLater(MinecraftDiscord.getInstance(),
                    runnable, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - reset) * 20);
        } else {
//            this.runnableQueue.add(new Pair<>(System.currentTimeMillis(), runnable));
            MinecraftDiscord.getInstance().getServer().getScheduler().runTask(MinecraftDiscord.getInstance(), runnable);
        }
    }

//    private void register(Runnable runnable) {
//        this.requestCount++;
//
//        if (this.requestCount == this.rateLimit && this.activated) {
//            long diff = (this.rateLimitReset * 1000) - System.currentTimeMillis();
//            if (diff >= 0) {
//                this.requestCount = 0;
//                this.runnableQueue.add(new Pair<>())
//            }
//        }
//
//        return 0;
//    }

}
