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
import io.github.jordieh.minecraftdiscord.util.ConfigSection;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldHandler {

    private final Logger logger = LoggerFactory.getLogger(WorldHandler.class);

    private static WorldHandler instance;

    @Getter private Map<String, Long> longMap;

    private WorldHandler() {
        Plugin plugin = MinecraftDiscord.getInstance();
        FileConfiguration configuration = plugin.getConfig();

        longMap = new HashMap<>();

        configuration.getConfigurationSection(ConfigSection.LINKED_WORLDS).getValues(false).forEach(((s, o) -> {
            if (o instanceof Long) {
                if (!((long) o <= 0L)) {
                    longMap.put(s, (Long) o);
                    logger.debug("Link between world {} and channel {} established", s, o);
                }
            } else {
                logger.warn("Detected unregistered world in linked-worlds: {} linked to channel {}", s, o);
            }
        }));
    }

    public static WorldHandler getInstance() {
        return instance == null ? instance = new WorldHandler() : instance;
    }

    public Optional<IChannel> getWorldChannel(World world) {
        String name = world.getName();
        if (!longMap.containsKey(name)) {
            return Optional.empty();
        }
        return ClientHandler.getInstance().getGuild().getChannels().stream()
                .filter(channel -> channel.getLongID() == longMap.get(name))
                .findFirst();
    }
}
