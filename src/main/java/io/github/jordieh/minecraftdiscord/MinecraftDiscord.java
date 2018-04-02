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

import io.github.jordieh.minecraftdiscord.command.LinkCommand;
import io.github.jordieh.minecraftdiscord.command.UnlinkCommand;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.discord.LinkHandler;
import io.github.jordieh.minecraftdiscord.discord.RoleHandler;
import io.github.jordieh.minecraftdiscord.discord.WebhookHandler;
import io.github.jordieh.minecraftdiscord.listeners.minecraft.AsyncPlayerChatListener;
import io.github.jordieh.minecraftdiscord.listeners.minecraft.PlayerJoinListener;
import io.github.jordieh.minecraftdiscord.listeners.minecraft.PlayerQuitListener;
import io.github.jordieh.minecraftdiscord.metrics.MetricsHandler;
import io.github.jordieh.minecraftdiscord.util.LangUtil;
import io.github.jordieh.minecraftdiscord.world.WorldHandler;
import lombok.Getter;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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

    private double startup;

    @Override
    public void onEnable() {
        startup = System.currentTimeMillis();
        System.out.println("============== [MinecraftDiscord] ==============");

        instance = this;

        logger.debug("Saving default configuration");
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        saveResource("language/messages_en.properties", false);
        saveResource("language/messages.properties", false);

        logger.debug("Registering handlers");
        ClientHandler.getInstance();
        logger.info("Waiting for the bot to be ready");
    }

    @Override
    public void onDisable() {
        RoleHandler.getInstance().clearRoleEnabledUsers(false);
        logger.debug("Plugin disable procedure has been engaged");
        LinkHandler.getInstance().saveResources();
        ClientHandler.getInstance().disable();
    }

    /**
     * Make sure the bot has started up before doing anything special
     */
    public void finishStartup() {
        if (startup == -1) {
            return;
        }

        LangUtil.getInstance();
        LinkHandler.getInstance();
        WebhookHandler.getInstance();
        MetricsHandler.getInstance();
        WorldHandler.getInstance();

        RoleHandler.getInstance().clearRoleEnabledUsers(false);
        RoleHandler.getInstance().giveLinkedUsersOnlineRole();

        logger.debug("Registering Bukkit events");
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);

        logger.debug("Registering commands");
        getCommand("link").setExecutor(new LinkCommand());
        getCommand("unlink").setExecutor(new UnlinkCommand());

        startup = ((System.currentTimeMillis() - startup)) / 1000.0d;
        NumberFormat format = new DecimalFormat("#0.00");
        logger.info("The plugin has been enabled in {} seconds", format.format(startup));
        System.out.println("============== [MinecraftDiscord] ==============");

        startup = -1;
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + "?");
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
//                logger.info("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            logger.info("Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

}
