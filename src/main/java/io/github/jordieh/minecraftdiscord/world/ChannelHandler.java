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
import io.github.jordieh.minecraftdiscord.dependencies.listeners.Dependency;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ChannelHandler {

    private final Logger logger = LoggerFactory.getLogger(ChannelHandler.class);

    private static ChannelHandler instance;

    @Getter private Map<String, Long> longMap;
    private Map<String, Long> integrationMap;

    private ChannelHandler() {
        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();

        this.longMap = configuration.getConfigurationSection("channels").getValues(false)
                .entrySet()
                .stream()
                .filter(e -> !e.getKey().contains("@")) // This indicates a plugin hook and should not be registered here
                .filter(e -> e.getValue() instanceof Long)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (Long) e.getValue()));

        this.integrationMap = configuration.getConfigurationSection("channels").getValues(false)
                .entrySet()
                .stream()
                .filter(e -> e.getKey().contains("@"))
                .filter(e -> e.getValue() instanceof Long)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (Long) e.getValue()));
    }

    public static ChannelHandler getInstance() {
        return instance == null ? instance = new ChannelHandler() : instance;
    }

    public Optional<IChannel> getIntegrationChannel(@NonNull String name, @NonNull Dependency dependency) {
        String channel = dependency.name + "@" + name;
        if (!this.integrationMap.containsKey(channel)) {
            return Optional.empty();
        }
        return Optional.ofNullable(ClientHandler.getInstance().getClient().getChannelByID(this.integrationMap.get(channel)));
    }

    public Optional<IChannel> getConnectedChannel(@NonNull String name) {
        if (!this.longMap.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.ofNullable(ClientHandler.getInstance().getClient().getChannelByID(this.longMap.get(name)));
    }
}
