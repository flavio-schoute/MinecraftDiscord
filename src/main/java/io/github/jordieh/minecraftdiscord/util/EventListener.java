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

package io.github.jordieh.minecraftdiscord.util;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import lombok.NonNull;
import net.jodah.typetools.TypeResolver;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class EventListener<T extends Event> implements Listener {

    final Logger logger = LoggerFactory.getLogger(EventListener.class);

    public EventListener(@NonNull Consumer<T> consumer) {
        Class<T> clazz = (Class<T>) TypeResolver.resolveRawArgument(Consumer.class, consumer.getClass());
        this.logger.info("Registering listener: {}", clazz.getSimpleName());

        Plugin plugin = MinecraftDiscord.getInstance();
        plugin.getServer().getPluginManager().registerEvent(clazz, this, EventPriority.NORMAL,
                ((listener, event) -> consumer.accept((clazz.cast(event)))), plugin);
    }
}
