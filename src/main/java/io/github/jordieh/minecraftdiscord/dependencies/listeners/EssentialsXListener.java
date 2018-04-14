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
import io.github.jordieh.minecraftdiscord.dependencies.DependencyListener;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.discord.RoleHandler;
import io.github.jordieh.minecraftdiscord.util.FormatUtil;
import io.github.jordieh.minecraftdiscord.world.ChannelHandler;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Optional;

public class EssentialsXListener extends DependencyListener {

    public EssentialsXListener() {
        super(Dependency.ESSENTIALSX);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onVanishStatusChange(VanishStatusChangeEvent event) {
        Optional<IChannel> channel = ChannelHandler.getInstance().getIntegrationChannel("events", dependency);
        if (!channel.isPresent()) {
            return;
        }

        Player player = event.getController().getBase();

        if (event.getValue()) {
            RoleHandler.getInstance().removeConnectionRole(player.getUniqueId());

            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorIcon(FormatUtil.avatarUrl(player.getUniqueId().toString()));
            builder.withAuthorName(player.getName());
            builder.withColor(0xFFAA00);
            builder.withDescription(player.getName() + " has vanished!");
            builder.withTimestamp(System.currentTimeMillis());
            builder.withFooterText(super.dependency.getName());
            builder.withFooterIcon(super.dependency.getIcon());

            ClientHandler.getInstance().sendMessage(channel.get(), builder.build());
        } else {
            RoleHandler.getInstance().giveConnectionRole(player.getUniqueId());

            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorIcon(FormatUtil.avatarUrl(player.getUniqueId().toString()));
            builder.withAuthorName(player.getName());
            builder.withColor(0x00AA00);
            builder.withDescription(player.getName() + " has appeared!");
            builder.withTimestamp(System.currentTimeMillis());
            builder.withFooterText(super.dependency.getName());
            builder.withFooterIcon(super.dependency.getIcon());

            ClientHandler.getInstance().sendMessage(channel.get(), builder.build());
        }
    }
}
