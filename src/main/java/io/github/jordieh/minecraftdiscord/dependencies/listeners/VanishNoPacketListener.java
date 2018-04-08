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

package io.github.jordieh.minecraftdiscord.dependencies.listeners;

import io.github.jordieh.minecraftdiscord.dependencies.Dependency;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.util.FormatUtil;
import io.github.jordieh.minecraftdiscord.world.ChannelHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Optional;

public class VanishNoPacketListener implements Listener {

    private final Dependency dependency;

    public VanishNoPacketListener() {
        this.dependency = Dependency.VANISHNOPACKET;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onVanishStatusChange(VanishStatusChangeEvent event) {
        Optional<IChannel> channel = ChannelHandler.getInstance().getIntegrationChannel("events", dependency);
        if (!channel.isPresent()) {
            return;
        }

        Player player = event.getPlayer();

        if (event.isVanishing()) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorIcon(FormatUtil.avatarUrl(player.getUniqueId().toString()));
            builder.withAuthorName(player.getName());
            builder.withColor(0xFFAA00);
            builder.withDescription(player.getName() + " has vanished!");
            builder.withTimestamp(System.currentTimeMillis());
            builder.withFooterText(this.dependency.getName());
            builder.withFooterIcon(this.dependency.getIcon());

            ClientHandler.getInstance().sendMessage(channel.get(), builder.build());
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorIcon(FormatUtil.avatarUrl(player.getUniqueId().toString()));
            builder.withAuthorName(player.getName());
            builder.withColor(0x00AA00);
            builder.withDescription(player.getName() + " has appeared!");
            builder.withTimestamp(System.currentTimeMillis());
            builder.withFooterText(this.dependency.getName());
            builder.withFooterIcon(this.dependency.getIcon());

            ClientHandler.getInstance().sendMessage(channel.get(), builder.build());
        }
    }


}
