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

package io.github.jordieh.minecraftdiscord.metrics;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsHandler {

    private final Logger logger = LoggerFactory.getLogger(MetricsHandler.class);

    private static MetricsHandler instance;

    private MetricsHandler() {
        logger.debug("Constructing MetricsHandler");
        Metrics metrics = new Metrics(MinecraftDiscord.getInstance());

        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();

        metrics.addCustomChart(new Metrics.SimplePie("message_type", () -> {
            return configuration.getString("message-format.type".toLowerCase(), "embed");
        }));

        metrics.addCustomChart(new Metrics.SimplePie("account_linking", () -> {
            return configuration.getString("account-linking.enabled".toLowerCase(), "true");
        }));

        metrics.addCustomChart(new Metrics.SimplePie("playing_role", () -> {
            return configuration.getString("account-linking.online-role.enabled".toLowerCase(), "false");
        }));

        metrics.addCustomChart(new Metrics.SimplePie("presence_enabled", () -> {
            return configuration.getString("presence.enabled".toLowerCase(), "webhook");
        }));

        metrics.addCustomChart(new Metrics.SimplePie("presence_status", () -> {
            return configuration.getString("presence.type-status".toLowerCase(), "online");
        }));

        metrics.addCustomChart(new Metrics.SimplePie("presence_activity", () -> {
            return configuration.getString("presence.type-activity".toLowerCase(), "playing");
        }));

    }

    public static MetricsHandler getInstance() {
        return instance == null ? instance = new MetricsHandler() : instance;
    }
}
