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
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChannelHandler {

    private final Logger logger = LoggerFactory.getLogger(ChannelHandler.class);

    private static ChannelHandler instance;

    @Getter private Map<String, Long> longMap;

    private ChannelHandler() {
        Plugin plugin = MinecraftDiscord.getInstance();
        FileConfiguration configuration = plugin.getConfig();

        longMap = configuration.getConfigurationSection("channels").getValues(false)
                .entrySet()
                .stream()
                .filter(e -> e.getValue() instanceof Long)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (Long) e.getValue()));
    }

    public static ChannelHandler getInstance() {
        return instance == null ? instance = new ChannelHandler() : instance;
    }

    public Optional<IChannel> getConnectedChannel(@NonNull String name) {
        if (!longMap.containsKey(name)) {
            return Optional.empty();
        }
        return ClientHandler.getInstance().getClient().getChannels().stream()
                .filter(channel -> channel.getLongID() == longMap.get(name))
                .findFirst();
    }
}
