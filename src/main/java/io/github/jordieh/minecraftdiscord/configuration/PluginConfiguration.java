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

package io.github.jordieh.minecraftdiscord.configuration;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class PluginConfiguration {

    private static final File DATAFOLDER = MinecraftDiscord.getInstance().getDataFolder();

    private FileConfiguration configuration;
    private String name;
    private File file;


    public PluginConfiguration(String name) {
        this.name = name;
        this.file = new File(DATAFOLDER, (name + ".yml"));
        saveConfig();
    }

    public void reloadConfig() {
        configuration = YamlConfiguration.loadConfiguration(file);

        try (InputStream stream = MinecraftDiscord.getInstance().getResource(name)) {
            if (stream == null) {
                return;
            }
            try (Reader reader = new InputStreamReader(stream)) {
                configuration.setDefaults(YamlConfiguration.loadConfiguration(reader));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if (configuration == null) {
            this.reloadConfig();
        }
        return configuration;
    }

    public void saveConfig() {
        try {
            getConfig().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig() {
        if (!file.exists()) {
            MinecraftDiscord.getInstance().saveResource(file.getAbsolutePath(), false);
        }
    }
}
