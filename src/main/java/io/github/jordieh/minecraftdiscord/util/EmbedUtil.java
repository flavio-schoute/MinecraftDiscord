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

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.Color;

public class EmbedUtil {

    public static EmbedObject createEmbed(String desc) {
        return new EmbedBuilder().withDescription(desc).build();
    }

    public static EmbedObject createEmbed(String desc, int color) {
        return new EmbedBuilder().withDescription(desc).withColor(color).build();
    }

    public static EmbedObject createEmbed(String desc, int color, String authorName, String authorIcon) {
        return new EmbedBuilder().withDescription(desc).withColor(color)
                .withAuthorIcon(authorIcon).withAuthorName(authorName).build();
    }

    public static EmbedObject createEmbed(String desc, Color color, String authorName, String authorIcon) {
        return new EmbedBuilder().withDescription(desc).withColor(color)
                .withAuthorIcon(authorIcon).withAuthorName(authorName).build();
    }
}
