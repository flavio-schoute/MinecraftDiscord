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

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

public class ClientHandler {

    private static ClientHandler instance;
    @Getter private IDiscordClient client;

    private ClientHandler() {
//        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();
//        String token = configuration.getString("token");

        String token = "Dacht het niet";

        ClientBuilder builder = new ClientBuilder();
        builder.withRecommendedShardCount();
        builder.withToken(token);
        builder.registerListener(this);
        client = builder.login();
    }

    public static ClientHandler getInstance() {
        return instance == null ? instance = new ClientHandler() : instance;
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
//        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();
//        this.updatePresence(configuration);
        this.updatePresence();
    }

//    @TODO: FIX IMPLEMENTEREN
    @Deprecated
    private void updatePresence() {

        StatusType status;
        try {
            status = StatusType.ONLINE;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println("1");
            status = StatusType.ONLINE;
        }

        ActivityType activity;
        try {
            activity = ActivityType.PLAYING;
        } catch (IllegalArgumentException e) {
            System.out.println("2");
            e.printStackTrace();
            activity = ActivityType.PLAYING;
        }

        // Streaming uses a separate method that we will not be allowing
        if (activity == ActivityType.STREAMING) {
            System.out.println("3");
            activity = ActivityType.PLAYING;
        }

        String text = "play.dusdavidgames.nl";

        try {
            this.client.changePresence(status, activity, text);

        }catch (Exception e) {
            e.printStackTrace();
        }
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
