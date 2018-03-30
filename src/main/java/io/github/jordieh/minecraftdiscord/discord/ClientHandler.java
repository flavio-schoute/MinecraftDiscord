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

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.testing.Properties;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

public class ClientHandler {

    private static ClientHandler instance;

    @Getter private IDiscordClient client;

    private ClientHandler() {
        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();
        String token = configuration.getString("token");

//        String token = Properties.getInstance().getProperty("token");

        ClientBuilder builder = new ClientBuilder();
        builder.withRecommendedShardCount();
        builder.withToken(token);
        builder.registerListener(this); // ReadyEvent
        try {
            this.client = builder.login();
        } catch (DiscordException e) {
            e.printStackTrace();
        }
    }

    public static ClientHandler getInstance() {
        return instance == null ? instance = new ClientHandler() : instance;
    }

    public void sendMessage(IChannel channel, String message) {
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(message);
            } catch (DiscordException | MissingPermissionsException e) {
                e.printStackTrace();
            }
        }).get();
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();
        this.updatePresence(configuration);
//        this.updatePresence();
    }

    @Deprecated
    private void updatePresence() {
        if (!Properties.getInstance().getProperty("enabled").equals("TRUE")) {
//            this.logger.debug("Discord presence is disabled");
            return;
        }

        StatusType status;
        try {
            status = StatusType.valueOf(Properties.getInstance().getProperty("status"));
        } catch (IllegalArgumentException e) {
//            this.logger.warn("Invalid status type '%s' detected in config", Properties.getInstance().getProperty("status"));
            status = StatusType.ONLINE;
        }

        ActivityType activity;
        try {
            activity = ActivityType.valueOf(Properties.getInstance().getProperty("activity"));
        } catch (IllegalArgumentException e) {
//            this.logger.warn("Invalid activity type '%s' detected in config", Properties.getInstance().getProperty("activity"));
            activity = ActivityType.PLAYING;
        }

        if (activity == ActivityType.STREAMING) {
//            this.logger.warn("Detected usage of ActivityType.STREAMING, this activity type is not supported!");
            activity = ActivityType.PLAYING;
        }

        String text = Properties.getInstance().getProperty("text");
        this.client.changePresence(status, activity, text);
    }

    private void updatePresence(FileConfiguration configuration) {
        if (!configuration.getBoolean("presence.enabled")) {
            return;
        }

        StatusType status;
        try {
            status = StatusType.valueOf(configuration.getString("presence.type-status").toUpperCase());
        } catch (IllegalArgumentException e) {
            // @TODO Send message to console
            status = StatusType.ONLINE;
        }

        ActivityType activity;
        try {
            activity = ActivityType.valueOf(configuration.getString("presence.type-activity").toUpperCase());
        } catch (IllegalArgumentException e) {
            // @TODO Send message to console
            activity = ActivityType.PLAYING;
        }

        // Streaming uses a separate method that we will not be allowing
        if (activity == ActivityType.STREAMING) {
            // @TODO Send message to console
            activity = ActivityType.PLAYING;
        }

        String text = configuration.getString("presence.text");

        this.client.changePresence(status, activity, text);
    }
}
