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

package io.github.jordieh.minecraftdiscord.discord;

import org.bukkit.World;
import sx.blah.discord.handle.obj.IWebhook;

import java.util.regex.Pattern;

public class WebhookHandler {

    public static final Pattern pattern = Pattern.compile("Minecraft -> Discord \\[(\\w+)]");

    public static IWebhook getWebhook(World world) {
        return ClientHandler.getInstance().getGuild().getWebhooks().stream()
                .filter(w -> pattern.matcher(w.getDefaultName()).matches())
                .filter(w -> pattern.matcher(w.getDefaultName()).group(1).equalsIgnoreCase(world.getName()))
                .findFirst().orElse(createWebhook(world));
    }

    public static IWebhook createWebhook(World world) {
        String name = "Minecraft -> Discord [" + world.getName() + "]";
        return ClientHandler.getInstance().getGuild().getChannelsByName(world.getName()).get(0).createWebhook(name);
    }
}
