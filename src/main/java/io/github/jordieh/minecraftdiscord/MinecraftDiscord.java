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
import lombok.Getter;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftDiscord extends JavaPlugin {

    @Getter private static MinecraftDiscord instance;

    @Override
    public void onEnable() {
        System.out.println("============== [MinecraftDiscord] ==============");
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        instance = this;
//        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        ClientHandler.getInstance();

        new AsyncPlayerChatListener();

        Metrics metrics = new Metrics(this);
        System.out.println("============== [MinecraftDiscord] ==============");
    }

    @Override
    public void onDisable() {
        ClientHandler.getInstance().getClient().logout();
    }

}
