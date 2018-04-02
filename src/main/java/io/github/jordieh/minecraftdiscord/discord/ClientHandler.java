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
import io.github.jordieh.minecraftdiscord.listeners.discord.MessageReceivedEventHandler;
import io.github.jordieh.minecraftdiscord.util.ConfigSection;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.util.Optional;

public class ClientHandler implements IListener<ReadyEvent> {

    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private static ClientHandler instance;

    @Getter private IDiscordClient client;
    @Getter private IGuild guild;
    private boolean disable;

    private ClientHandler() {
        logger.debug("Constructing ClientHandler");
        MinecraftDiscord plugin = MinecraftDiscord.getInstance();
        FileConfiguration configuration = plugin.getConfig();
        String token = configuration.getString(ConfigSection.TOKEN);

        logger.trace("Starting ClientBuilder");
        ClientBuilder builder = new ClientBuilder();
        builder.withRecommendedShardCount();
        builder.withToken(token);
        builder.registerListener(this); // ReadyEvent
        builder.registerListener(new MessageReceivedEventHandler()); // MessageReceivedEvent

        try {
            logger.debug("Trying to connect to Discord");
            client = builder.login();
            logger.debug("Successfully connected to Discord with {} listeners", 1);
        } catch (DiscordException e) {
            if (e.getMessage().contains("401") && configuration.getBoolean(ConfigSection.FIRST_STARTUP)) {
                logger.error("\n#############################################\n" +
                        "# First startup error detected              #\n" +
                        "# You seem to have a invalid bot token      #\n" +
                        "# Please visit ... and change your token!   #\n" +
                        "#############################################");
                logger.trace("Updating startup path in config.yml");
                configuration.set(ConfigSection.FIRST_STARTUP, false);
                plugin.saveConfig();
                this.disable();
            } else {
                logger.warn("Error detected while attempting Discord connection", e);
            }
        }
    }

    public static ClientHandler getInstance() {
        return instance == null ? instance = new ClientHandler() : instance;
    }

    public void giveRole(IRole role, IUser user) {
        RequestBuffer.request(() -> {
            try {
                logger.trace("Attempting to give user {} role {} ({})", user.getLongID(), role.getName(), role.getLongID());
                user.addRole(role);
            } catch (DiscordException | MissingPermissionsException e) {
                e.printStackTrace();
            }
        }).get();
    }

    public void removeRole(IRole role, IUser user) {
        RequestBuffer.request(() -> {
            try {
                logger.trace("Attempting to remove role {} [{}] from user {}", role.getName(), role.getLongID(), user.getLongID());
                user.removeRole(role);
            } catch (DiscordException | MissingPermissionsException e) {
                e.printStackTrace();
            }
        }).get();
    }

    public void deleteMessage(IMessage message) {
        RequestBuffer.request(() -> {
            try {
                logger.trace("Attempting to delete message {} in {}", message.getLongID(), message.getChannel().getName());
                message.delete();
            } catch (DiscordException | MissingPermissionsException e) {
                e.printStackTrace();
            }
        }).get();
    }

    public void sendMessage(IChannel channel, String message) {
        RequestBuffer.request(() -> {
            try {
                logger.trace("Attempting to send '{}' to {}", message, channel.getName());
                channel.sendMessage(message);
            } catch (DiscordException | MissingPermissionsException e) {
                e.printStackTrace();
            }
        }).get();
    }

    public void sendMessage(IChannel channel, EmbedObject embed) {
        RequestBuffer.request(() -> {
            try {
                logger.trace("Attempting to send an embed to {}", channel.getName());
                channel.sendMessage(embed);
            } catch (DiscordException | MissingPermissionsException e) {
                e.printStackTrace();
            }
        }).get();
    }

    public void disable() {
        if (client == null || !client.isReady()) {
            logger.trace("Waiting for ReadyEvent to call ClientHandler#disable();");
            disable = true;
            return;
        }

        if (guild != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withDescription(":closed_book: The server has been disabled");
            builder.withColor(0xFF5555);
            findConfigChannel(ConfigSection.SHUTDOWN_CHANNEL)
                    .ifPresent(channel -> sendMessage(channel, builder.build()));
        }
        logger.info("Disabling plugin: Read previous output for more information");
        client.logout();

        logger.debug("Disabling plugin via ClientHandler#disable();");
        Plugin plugin = MinecraftDiscord.getInstance();
        plugin.getServer().getPluginManager().disablePlugin(MinecraftDiscord.getInstance());
    }

    public Optional<IChannel> findConfigChannel(String path) {
        logger.trace("Attempting to find channel in guild {} using config section {}", this.guild.getLongID(), path);
        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();
        long configurationLong = configuration.getLong(path);
        IChannel iChannel = this.guild.getChannelByID(configurationLong);
        return Optional.ofNullable(iChannel);
    }

    @Override
    public void handle(ReadyEvent event) {
        logger.debug("ReadyEvent has been called");


        logger.trace("Retrieving guild id from config.yml");
        long configurationLong = MinecraftDiscord.getInstance().getConfig().getLong(ConfigSection.GUILD);
        guild = client.getGuildByID(configurationLong);
        if (guild == null) {
            logger.warn("Detected an invalid guild token: {}", configurationLong);
            disable();
        }

        if (this.disable) {
            this.disable();
            return;
        }

        MinecraftDiscord.getInstance().finishStartup();

        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();
        this.updatePresence(configuration);

        this.findConfigChannel(ConfigSection.SHUTDOWN_CHANNEL).ifPresent(channel -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withDescription(":green_book: The server has been turned on");
            builder.withColor(0x00AA00);
            this.sendMessage(channel, builder.build());
        });
    }

    private void updatePresence(FileConfiguration configuration) {
        logger.trace("Attempting to update Discord presence");
        if (!configuration.getBoolean(ConfigSection.PRESENCE_ENABLED)) {
            logger.debug("Discord presence is disabled in config.yml");
            return;
        }
        String state;

        StatusType status;
        state = configuration.getString(ConfigSection.PRESENCE_STATUS).toUpperCase();
        try {
            status = StatusType.valueOf(state);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid presence status type detected in config.yml ({}) using ONLINE", state);
            status = StatusType.ONLINE;
        }

        ActivityType activity;
        state = configuration.getString(ConfigSection.PRESENCE_ACTIVITY).toUpperCase();
        try {
            activity = ActivityType.valueOf(state);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid presence activity type detected in config.yml ({}) using PLAYING", state);
            activity = ActivityType.PLAYING;
        }

        // Streaming uses a separate method that we will not be allowing
        if (activity == ActivityType.STREAMING) {
            logger.warn("Detected usage of activity type STREAMING in config.yml, this is unsupported, using PLAYING");
            activity = ActivityType.PLAYING;
        }

        String text = configuration.getString(ConfigSection.PRESENCE_TEXT);

        logger.debug("Attempting to change presence [{}] [{}] [{}]", status.name(), activity.name(), text);
        this.client.changePresence(status, activity, text);
    }
}
