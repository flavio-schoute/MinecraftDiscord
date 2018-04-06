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
import org.bukkit.ChatColor;

public final class FormatUtil {

    public static String stripColors(String s) {
        return s.replaceAll("(&[\\da-fA-Fk-oK-OrR]|[\\n\\r]*+)", "")
                .replaceAll("\\s{4,}", " ");
    }

    public static String formatColors(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String avatarUrl(String uuid) {
        return MinecraftDiscord.getInstance().getConfig().getString("options.message-render")
                .replace("<uuid>", uuid);
    }

    /**
     * Truncates a String object to a certain length (n)
     * @param s The String object to truncate
     * @param n The maximum number of allowed characters
     * @return The content of String s with length n
     */
    public static String truncateString(@NonNull String s, @NonNull int n) {
        return s.substring(0, Math.min(s.length(), n));
    }
}
