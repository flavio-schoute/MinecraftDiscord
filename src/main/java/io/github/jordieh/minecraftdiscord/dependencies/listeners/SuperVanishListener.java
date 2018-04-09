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

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import io.github.jordieh.minecraftdiscord.dependencies.Dependency;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.discord.RoleHandler;
import io.github.jordieh.minecraftdiscord.util.FormatUtil;
import io.github.jordieh.minecraftdiscord.world.ChannelHandler;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Optional;

/**
 * Integration for SuperVanish & PremiumVanish
 */
public class SuperVanishListener implements Listener {

    private final Dependency dependency;

    public SuperVanishListener(@NonNull Dependency dependency) {
        this.dependency = dependency;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerHide(PlayerHideEvent event) {
        Optional<IChannel> channel = ChannelHandler.getInstance().getIntegrationChannel("events", dependency);
        if (!channel.isPresent()) {
            return;
        }

        String s = event.isSilent() ? " silently" : "";

        Player player = event.getPlayer();

        RoleHandler.getInstance().removeConnectionRole(player.getUniqueId());

        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorIcon(FormatUtil.avatarUrl(player.getUniqueId().toString()));
        builder.withAuthorName(player.getName());
        builder.withColor(0xFFAA00);
        builder.withDescription(player.getName() + " has vanished" + s + "!");
        builder.withTimestamp(System.currentTimeMillis());
        builder.withFooterText(this.dependency.getName());
        builder.withFooterIcon(this.dependency.getIcon());

        ClientHandler.getInstance().sendMessage(channel.get(), builder.build());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerShow(PlayerShowEvent event) {
        Optional<IChannel> channel = ChannelHandler.getInstance().getIntegrationChannel("events", dependency);
        if (!channel.isPresent()) {
            return;
        }

        String s = event.isSilent() ? " silently" : "";

        Player player = event.getPlayer();

        RoleHandler.getInstance().giveConnectionRole(player.getUniqueId());

        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorIcon(FormatUtil.avatarUrl(player.getUniqueId().toString()));
        builder.withAuthorName(player.getName());
        builder.withColor(0x00AA00);
        builder.withDescription(player.getName() + " has appeared" + s + "!");
        builder.withTimestamp(System.currentTimeMillis());
        builder.withFooterText(this.dependency.getName());
        builder.withFooterIcon(this.dependency.getIcon());

        ClientHandler.getInstance().sendMessage(channel.get(), builder.build());
    }

}
