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

import de.robingrether.idisguise.api.DisguiseEvent;
import de.robingrether.idisguise.api.OfflinePlayerDisguiseEvent;
import de.robingrether.idisguise.api.OfflinePlayerUndisguiseEvent;
import de.robingrether.idisguise.api.UndisguiseEvent;
import de.robingrether.idisguise.disguise.Disguise;
import io.github.jordieh.minecraftdiscord.dependencies.Dependency;
import io.github.jordieh.minecraftdiscord.dependencies.DependencyListener;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.util.FormatUtil;
import io.github.jordieh.minecraftdiscord.world.ChannelHandler;
import org.apache.commons.lang.WordUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Optional;

public class IDisguiseListener extends DependencyListener {

    public IDisguiseListener() {
        super(Dependency.IDISGUISE);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDisguise(DisguiseEvent event) {
        Optional<IChannel> channel = ChannelHandler.getInstance().getIntegrationChannel("disguise", dependency);
        if (!channel.isPresent()) {
            return;
        }

        Disguise disguise = event.getDisguise();
        Player player = event.getPlayer();

        if (disguise.getType() == null) {
            return;
        }

        String name = WordUtils.capitalize(disguise.getType().getDefaultCommandArgument().replace("_", " "));

        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorIcon(FormatUtil.avatarUrl(player.getUniqueId().toString()));
        builder.withAuthorName(player.getName());
        builder.withColor(0xFFAA00);
        builder.withDescription(player.getName() + " has disguised into a " + name + "!");
        builder.withTimestamp(System.currentTimeMillis());
        builder.withFooterText(super.dependency.getName());
        builder.withFooterIcon("https://jordieh.github.io/MinecraftDiscord/idisguise/" + event.getDisguise().getType().getDefaultCommandArgument() + ".png");

        ClientHandler.getInstance().sendMessage(channel.get(), builder.build());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onOfflinePlayerDisguise(OfflinePlayerDisguiseEvent event) {
        Optional<IChannel> channel = ChannelHandler.getInstance().getIntegrationChannel("disguise", dependency);
        if (!channel.isPresent()) {
            return;
        }

        Disguise disguise = event.getDisguise();
        OfflinePlayer offlinePlayer = event.getPlayer();

        if (disguise.getType() == null) {
            return;
        }

        String name = WordUtils.capitalize(disguise.getType().getDefaultCommandArgument().replace("_", " "));

        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorIcon(FormatUtil.avatarUrl(offlinePlayer.getUniqueId().toString()));
        builder.withAuthorName(offlinePlayer.getName());
        builder.withColor(0xFFAA00);
        builder.withDescription(offlinePlayer.getName() + " has disguised into a " + name + " while offline!");
        builder.withTimestamp(System.currentTimeMillis());
        builder.withFooterText(super.dependency.getName());
        builder.withFooterIcon("https://jordieh.github.io/MinecraftDiscord/idisguise/" + event.getDisguise().getType().getDefaultCommandArgument() + ".png");

        ClientHandler.getInstance().sendMessage(channel.get(), builder.build());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onUndisguise(UndisguiseEvent event) {
        Optional<IChannel> channel = ChannelHandler.getInstance().getIntegrationChannel("undisguise", dependency);
        if (!channel.isPresent()) {
            return;
        }

        Disguise disguise = event.getDisguise();
        Player player = event.getPlayer();

        if (disguise.getType() == null) {
            return;
        }

        String name = WordUtils.capitalize(disguise.getType().getDefaultCommandArgument().replace("_", " "));

        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorIcon(FormatUtil.avatarUrl(player.getUniqueId().toString()));
        builder.withAuthorName(player.getName());
        builder.withColor(0xFF5555);
        builder.withDescription(player.getName() + " has undisguised from a " + name + "!");
        builder.withTimestamp(System.currentTimeMillis());
        builder.withFooterText(super.dependency.getName());
        builder.withFooterIcon("https://jordieh.github.io/MinecraftDiscord/idisguise/" + event.getDisguise().getType().getDefaultCommandArgument() + ".png");

        ClientHandler.getInstance().sendMessage(channel.get(), builder.build());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onOfflinePlayerUndisguise(OfflinePlayerUndisguiseEvent event) {
        Optional<IChannel> channel = ChannelHandler.getInstance().getIntegrationChannel("undisguise", dependency);
        if (!channel.isPresent()) {
            return;
        }

        Disguise disguise = event.getDisguise();
        OfflinePlayer offlinePlayer = event.getPlayer();

        if (disguise.getType() == null) {
            return;
        }

        String name = WordUtils.capitalize(disguise.getType().getDefaultCommandArgument().replace("_", " "));

        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorIcon(FormatUtil.avatarUrl(offlinePlayer.getUniqueId().toString()));
        builder.withAuthorName(offlinePlayer.getName());
        builder.withColor(0xFF5555);
        builder.withDescription(offlinePlayer.getName() + " has undisguised from a " + name + " while offline!");
        builder.withTimestamp(System.currentTimeMillis());
        builder.withFooterText(super.dependency.getName());
        builder.withFooterIcon("https://jordieh.github.io/MinecraftDiscord/idisguise/" + event.getDisguise().getType().getDefaultCommandArgument() + ".png");

        ClientHandler.getInstance().sendMessage(channel.get(), builder.build());
    }
}
