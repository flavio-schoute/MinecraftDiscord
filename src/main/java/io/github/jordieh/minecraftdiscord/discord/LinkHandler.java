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

import io.github.jordieh.minecraftdiscord.configuration.PluginConfiguration;
import io.github.jordieh.minecraftdiscord.util.FormatUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.jordieh.minecraftdiscord.util.LangUtil.tr;

public class LinkHandler {

    private final Logger logger = LoggerFactory.getLogger(LinkHandler.class);

    private static LinkHandler instance;

    private PluginConfiguration configuration;
    private Map<Integer, UUID> uuidMap;
    @Getter private Map<Long, UUID> linkMap;

    private LinkHandler() {
        logger.debug("Constructing LinkHandler");
        linkMap = new HashMap<>();
        uuidMap = new HashMap<>();

        configuration = new PluginConfiguration("accounts");
        ConfigurationSection section = configuration.getConfig().getConfigurationSection("accounts");
        if (section == null) {
            return;
        }

        Map<String, Object> objectMap = section.getValues(false);

        objectMap.forEach((s, o) -> {
            if (o instanceof String) {
                linkMap.put(Long.valueOf(s), UUID.fromString((String) o));
            }
        });
    }

    public static LinkHandler getInstance() {
        return instance == null ? instance = new LinkHandler() : instance;
    }

    public void saveResources() {
        Map<Long, String> stringMap = new HashMap<>();
        linkMap.forEach((l, uuid) -> stringMap.put(l, uuid.toString()));

        configuration.getConfig().set("accounts", stringMap);
        configuration.saveConfig();
    }

    public boolean isLinked(UUID uuid) {
        return linkMap.containsValue(uuid);
    }

    public boolean isLinked(long id) {
        return linkMap.containsKey(id);
    }

    public long getLinkedUser(UUID uuid) {
        return linkMap.entrySet().stream()
                .filter(e -> e.getValue().equals(uuid))
                .findFirst().orElseThrow(() -> new NullPointerException("Missing LinkHandler#isLinked(); method"))
                .getKey();
    }

    public boolean linkAccount(IUser user, int code) {
        logger.debug("Trying to link user {} with a Minecraft account", user.getLongID());
        if (!uuidMap.containsKey(code)) {
            logger.trace("Linking user {} has failed (Incorrect code)", user.getLongID());
            return false;
        }
        UUID uuid = uuidMap.get(code);
        linkMap.put(user.getLongID(), uuid);
        uuidMap.remove(code);
        logger.debug("Successfully linked user M:{} to D:{} using code {}", uuid.toString(), user.getLongID(), code);
        return true;
    }

    public int generateCode(UUID uuid) {
        String uuidString = uuid.toString();
        logger.debug("Generating code for {}", uuidString);
        if (uuidMap.containsValue(uuid)) {
            logger.trace("Linking system already had code for {} generated", uuidString);
            return uuidMap.entrySet().stream()
                    .filter(e -> e.getValue().equals(uuid))
                    .findFirst().orElseThrow(() -> new NullPointerException("This should never happen?"))
                    .getKey();
        }
        int x;
        while (true) {
            x = generateCode();
            if (!uuidMap.containsKey(x)) {
                logger.debug("Generated code for {}", uuidString);
                uuidMap.put(x, uuid);
                break;
            }
            logger.trace("Generated duplicate code for {} [{}]", uuidString, x);
        }
        return x;
    }

    private int generateCode() {
        return 100000 + ((int) (ThreadLocalRandom.current().nextFloat() * 900000.0f));
    }

    public Optional<UUID> unlink(UUID uuid) {
        if (isLinked(uuid)) {
            linkMap.remove(getLinkedUser(uuid));
            return Optional.of(uuid);
        }
        return Optional.empty();
    }

    public Optional<UUID> unlink(long id) {
        if (linkMap.containsKey(id)) {
            UUID temp = linkMap.get(id);
            linkMap.remove(id);
            return Optional.of(temp);
        }
        return Optional.empty();
    }

    public boolean handleLinking(MessageReceivedEvent event) {
        IChannel channel = event.getChannel();
        IMessage message = event.getMessage();
        String content = message.getContent();
        IUser user = event.getAuthor();

        if (content.startsWith("/unlink")) {
            Optional<UUID> success = unlink(user.getLongID());
            if (success.isPresent()) {
                UUID uuid = success.get();
                String temp = uuid.toString();
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                Player player = offlinePlayer.getPlayer();
                if (player != null) {
                    String usableTag = user.getName() + "#" + user.getDiscriminator();
                    player.sendMessage(tr("discord.unlink.minecraft", usableTag));
                }
                EmbedBuilder builder = new EmbedBuilder();
                builder.withDescription(tr("discord.unlink.success"));
                builder.withColor(user.getColorForGuild(event.getGuild()));
                builder.withAuthorName(tr("discord.unlink.success.title", temp, offlinePlayer.getName()));
                builder.withAuthorIcon(FormatUtil.avatarUrl(temp));
                ClientHandler.getInstance().deleteMessage(message);
                ClientHandler.getInstance().sendMessage(channel, builder.build());

                RoleHandler.getInstance().removeOnlineRole(uuid);
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.withDescription(tr("discord.unlink.failed"));
                builder.withColor(0xFF5555);
                ClientHandler.getInstance().deleteMessage(message);
                ClientHandler.getInstance().sendMessage(channel, builder.build());
            }
            return true;
        }

        if (!content.startsWith("/link")) {
            return false;
        }

        if (linkMap.containsKey(user.getLongID())) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withDescription(tr("discord.link.failed", linkMap.get(user.getLongID()).toString()));
            builder.withColor(0xFF5555);
            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel, builder.build());
            return true;
        }

        Matcher matcher = Pattern.compile("/link\\s*(\\d{6})").matcher(content);

        if (!matcher.matches()) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withDescription(tr("discord.link.usage"));
            builder.withColor(0xFF5555);
            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel, builder.build());
            return true;
        }

        int code = Integer.parseInt(matcher.group(1));

        if (!linkAccount(user, code)) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withDescription(tr("discord.link.invalid"));
            builder.withColor(0xFF5555);
            ClientHandler.getInstance().deleteMessage(message);
            ClientHandler.getInstance().sendMessage(channel, builder.build());
            return true;
        }

        UUID uuid = linkMap.get(user.getLongID());
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String temp = uuid.toString();

        EmbedBuilder builder = new EmbedBuilder();
        builder.withDescription(tr("discord.link.success"));
        builder.withColor(user.getColorForGuild(event.getGuild()));
        builder.withAuthorName(tr("discord.link.success.title", temp, offlinePlayer.getName()));
        builder.withAuthorIcon(FormatUtil.avatarUrl(temp));
        ClientHandler.getInstance().deleteMessage(message);
        ClientHandler.getInstance().sendMessage(channel, builder.build());

        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            String usableTag = user.getName() + "#" + user.getDiscriminator();
            player.sendMessage(tr("discord.link.success.minecraft", usableTag));
        }

        RoleHandler.getInstance().giveOnlineRole(uuid);

        return true;
    }
}
