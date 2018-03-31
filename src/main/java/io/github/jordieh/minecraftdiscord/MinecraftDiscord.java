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

package io.github.jordieh.minecraftdiscord;

import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.listeners.AsyncPlayerChatListener;
import io.github.jordieh.minecraftdiscord.metrics.MetricsHandler;
import lombok.Getter;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public final class MinecraftDiscord extends JavaPlugin {

    private final Logger logger = LoggerFactory.getLogger(MinecraftDiscord.class);

    // Resolving log4j dependency TODO Find a way to fix this in the pom.xml
    // InputStream in = getClass().getClassLoader().getResourceAsStream("log4j.properties");
    // PropertyConfigurator.configure(in);
    static {
        ConsoleAppender appender = new ConsoleAppender();
        PatternLayout layout = new PatternLayout();
        FileAppender fileAppender = new FileAppender();
    }

    @Getter private static MinecraftDiscord instance;

    @Override
    public void onEnable() {
        double startup = System.currentTimeMillis();
        System.out.println("============== [MinecraftDiscord] ==============");

        instance = this;

        logger.trace("Implementing default config configuration");
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Map<String, Long> longMap = new HashMap<>();
        World world = Bukkit.getWorld("world");
        longMap.put(world.getName(), 429073131869569044L);
        longMap.put("nether", 429073131869544L);
        getConfig().set("linked-worlds", longMap);
        saveConfig();

        new AsyncPlayerChatListener();

        MetricsHandler.getInstance();
        ClientHandler.getInstance();

        startup = ((System.currentTimeMillis() - startup)) / 1000.0d;
        NumberFormat format = new DecimalFormat("#0.00");
        logger.info("The plugin has been enabled in {} seconds", format.format(startup));
        System.out.println("============== [MinecraftDiscord] ==============");
    }

    @Override
    public void onDisable() {
        logger.debug("Plugin disable procedure has been engaged");
        saveConfig();
        ClientHandler.getInstance().disable();
    }

}
