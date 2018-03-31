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

/**
 * Utility enum for exact config.yml path selection
 */
public enum ConfigSection {

    TOKEN("token"),
    GUILD("guild"),

    PRESENCE_ENABLED("presence.enabled"),
    PRESENCE_STATUS("presence.type-status"),
    PRESENCE_ACTIVITY("presence.type-activity"),
    PRESENCE_TEXT("presence.text"),

    OUTPUT_TYPE("output-type"),

    CONSOLE_CHANNEL("console-channel"),
    SHUTDOWN_CHANNEL("shutdown-channel"),

    FIRST_STARTUP("first-startup");

    public final String PATH;

    ConfigSection(String PATH) {
        this.PATH = PATH;
    }
}
