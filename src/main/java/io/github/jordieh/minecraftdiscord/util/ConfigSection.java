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
 * Utility class for exact config.yml path selection
 */
public class ConfigSection {

    public static final String TOKEN = "token";
    public static final String GUILD = "guild";

    public static final String PRESENCE_ENABLED = "presence.enabled";
    public static final String PRESENCE_STATUS = "presence.type-status";
    public static final String PRESENCE_ACTIVITY = "presence.type-activity";
    public static final String PRESENCE_TEXT = "presence.text";

    public static final String OUTPUT_TYPE = "message-format.type";
    public static final String RENDER_LINK = "message-format.render";
    public static final String DISCORD_FORMAT = "message-format.discord";

    public static final String CONSOLE_CHANNEL = "console-channel";
    public static final String SHUTDOWN_CHANNEL = "shutdown-channel";

    public static final String LINKING_ENABLED = "account-linking.enabled";
    public static final String LINKING_NEEDED = "account-linking.needed";
    public static final String ROLE_ENABLED = "account-linking.online-role.enabled";
    public static final String ROLE_UID = "account-linking.online-role.role";

    public static final String LINKED_WORLDS = "linked-worlds";

    public static final String FIRST_STARTUP = "first-startup";

}
