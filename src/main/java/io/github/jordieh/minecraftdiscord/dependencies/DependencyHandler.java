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

package io.github.jordieh.minecraftdiscord.dependencies;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.dependencies.listeners.Dependency;
import io.github.jordieh.minecraftdiscord.dependencies.listeners.SuperVanishListener;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.List;

public class DependencyHandler {

    private static DependencyHandler instance;

    private List<String> disabledHooks;

    private DependencyHandler() {
        MinecraftDiscord plugin = MinecraftDiscord.getInstance();

        this.disabledHooks = plugin.getConfig().getStringList("disabled-dependencies");

        if (isPluginEnabled(Dependency.SUPERVANISH)) {
            plugin.getServer().getPluginManager().registerEvents(new SuperVanishListener(Dependency.SUPERVANISH), plugin);
        }

        if (isPluginEnabled(Dependency.PREMIUMVANISH)) {
            plugin.getServer().getPluginManager().registerEvents(new SuperVanishListener(Dependency.PREMIUMVANISH), plugin);
        }
    }

    public static DependencyHandler getInstance() {
        return instance == null ? instance = new DependencyHandler() : instance;
    }

    public boolean isPluginEnabled(@NonNull Dependency dependency) {
        return Bukkit.getPluginManager().isPluginEnabled(dependency.name) && !this.disabledHooks.contains(dependency.name);
    }

}
