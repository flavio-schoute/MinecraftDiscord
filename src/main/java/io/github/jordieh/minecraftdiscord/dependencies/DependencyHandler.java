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

import com.earth2me.essentials.Essentials;
import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.dependencies.listeners.EssentialsXListener;
import io.github.jordieh.minecraftdiscord.dependencies.listeners.IDisguiseListener;
import io.github.jordieh.minecraftdiscord.dependencies.listeners.SuperVanishListener;
import io.github.jordieh.minecraftdiscord.dependencies.listeners.VanishNoPacketListener;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyHandler {

    private final Logger logger = LoggerFactory.getLogger(DependencyHandler.class);

    private static DependencyHandler instance;

    private Set<Dependency> enabledHooks;
    private List<String> disabledHooks;

    private Essentials essentials;

    private DependencyHandler() {
        MinecraftDiscord plugin = MinecraftDiscord.getInstance();

        this.enabledHooks = new HashSet<>();
        this.disabledHooks = plugin.getConfig().getStringList("disabled-dependencies");

        this.integrate(Dependency.SUPERVANISH, plugin, new SuperVanishListener(Dependency.SUPERVANISH));
        this.integrate(Dependency.PREMIUMVANISH, plugin, new SuperVanishListener(Dependency.PREMIUMVANISH));
        this.integrate(Dependency.VANISHNOPACKET, plugin, new VanishNoPacketListener());
        this.integrate(Dependency.IDISGUISE, plugin, new IDisguiseListener());

        if (this.integrate(Dependency.ESSENTIALSX, plugin, new EssentialsXListener())) { // TODO: Check if NOT EssentialsX
            this.essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        }

    }

    public static DependencyHandler getInstance() {
        return instance == null ? instance = new DependencyHandler() : instance;
    }

    private boolean isPluginEnabled(@NonNull Dependency dependency) {
        return Bukkit.getPluginManager().isPluginEnabled(dependency.getName()) && !this.disabledHooks.contains(dependency.getName());
    }

    public boolean isVanished(Player player) {
        for (MetadataValue metadataValue : player.getMetadata("vanished")) { // SuperVanish, PremiumVanish & VanishNoPacket
            if (metadataValue.asBoolean()) {
                return true;
            }
        }

        // FIXME player.hasPermission("essentials.silentjoin")? - Essentials is sh*t
        return essentials != null && (essentials.getUser(player).isVanished());
    }

    private boolean integrate(@NonNull Dependency dependency, @NonNull Plugin plugin, Listener listener) {
        if (this.isPluginEnabled(dependency)) {
            if (listener != null) {
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
            }
            this.enabledHooks.add(dependency);
            logger.info("{} integration enabled", dependency.getName());
            return true;
        }
        return false;
    }

}
