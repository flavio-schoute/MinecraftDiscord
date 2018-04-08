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

package io.github.jordieh.minecraftdiscord.dependencies.listeners;

public enum Dependency {

    @Deprecated PREMIUMVANISH("PremiumVanish", "14/14404.jpg?1447087910"), // SuperVanish shares the same api usage
    SUPERVANISH("SuperVanish", "1/1331.jpg?141236592");

    public final String name;
    public final String icon;

    Dependency(String name, String icon) {
        this.name = name;
        this.icon = "https://www.spigotmc.org/data/resource_icons/" + icon;
    }

}
