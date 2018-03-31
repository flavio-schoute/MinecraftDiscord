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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsHandler {

    private final Logger logger = LoggerFactory.getLogger(MetricsHandler.class);

    private static MetricsHandler instance;

    private MetricsHandler() {
        logger.debug("Constructing MetricsHandler");
        Metrics metrics = new Metrics(MinecraftDiscord.getInstance());
    }

    public static MetricsHandler getInstance() {
        return instance == null ? instance = new MetricsHandler() : instance;
    }
}