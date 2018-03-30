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

package io.github.jordieh.minecraftdiscord.listeners;

import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.util.EventListener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener extends EventListener<AsyncPlayerChatEvent> {

    // @TODO: Do something
    public AsyncPlayerChatListener() {
        super(event -> ClientHandler.getInstance().sendMessage(
                ClientHandler.getInstance().getClient()
                        .getGuilds().get(0).getChannelsByName(event.getPlayer().getWorld().getName()).get(0),
                event.getMessage()));
    }
}
