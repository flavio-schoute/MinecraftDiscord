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
import io.github.jordieh.minecraftdiscord.dependencies.listeners.IDisguiseListener;
import io.github.jordieh.minecraftdiscord.dependencies.listeners.SuperVanishListener;
import io.github.jordieh.minecraftdiscord.dependencies.listeners.VanishNoPacketListener;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyHandler {

    private static DependencyHandler instance;

    private Set<Dependency> enabledHooks;
    private List<String> disabledHooks;

    private DependencyHandler() {
        MinecraftDiscord plugin = MinecraftDiscord.getInstance();

        this.enabledHooks = new HashSet<>();
        this.disabledHooks = plugin.getConfig().getStringList("disabled-dependencies");

        if (isPluginEnabled(Dependency.SUPERVANISH)) {
            plugin.getServer().getPluginManager().registerEvents(new SuperVanishListener(Dependency.SUPERVANISH), plugin);
            this.enabledHooks.add(Dependency.SUPERVANISH);
        }

        if (isPluginEnabled(Dependency.PREMIUMVANISH)) {
            plugin.getServer().getPluginManager().registerEvents(new SuperVanishListener(Dependency.PREMIUMVANISH), plugin);
            this.enabledHooks.add(Dependency.PREMIUMVANISH);
        }

        if (isPluginEnabled(Dependency.VANISHNOPACKET)) {
            plugin.getServer().getPluginManager().registerEvents(new VanishNoPacketListener(), plugin);
            this.enabledHooks.add(Dependency.VANISHNOPACKET);
        }

        if (isPluginEnabled(Dependency.IDISGUISE)) {
            plugin.getServer().getPluginManager().registerEvents(new IDisguiseListener(), plugin);
            this.enabledHooks.add(Dependency.IDISGUISE);
        }
    }

    public static DependencyHandler getInstance() {
        return instance == null ? instance = new DependencyHandler() : instance;
    }

    public boolean isPluginEnabled(@NonNull Dependency dependency) {
        return Bukkit.getPluginManager().isPluginEnabled(dependency.getName()) && !this.disabledHooks.contains(dependency.getName());
    }

    public boolean isVanished(Player player) {
        for (MetadataValue metadataValue : player.getMetadata("vanished")) { // SuperVanish, PremiumVanish & VanishNoPacket
            if (metadataValue.asBoolean()) {
                return true;
            }
        }
        return false;
    }

}
