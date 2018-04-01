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

@Deprecated
public class EventListener {

//    final Logger logger = LoggerFactory.getLogger(EventListener.class);
//
//    public EventListener(Class<? extends Event> clazz) {
//        this.logger.info("Registering listener: {}", clazz.getSimpleName());
//
//        Plugin plugin = MinecraftDiscord.getInstance();
//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
//    }

//    public EventListener(@NonNull Consumer<T> consumer) {
//        Class<T> clazz = (Class<T>) TypeResolver.resolveRawArgument(Consumer.class, consumer.getClass());
//        this.logger.info("Registering listener: {}", clazz.getSimpleName());
//
//        Plugin plugin = MinecraftDiscord.getInstance();
//        plugin.getServer().getPluginManager().registerEvent(clazz, this, EventPriority.NORMAL,
//                ((listener, event) -> consumer.accept((clazz.cast(event)))), plugin);
//    }
}
