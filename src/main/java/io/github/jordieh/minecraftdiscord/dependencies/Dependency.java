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

import io.github.jordieh.minecraftdiscord.util.Pair;
import lombok.NonNull;

public enum Dependency {

    // SuperVanish & PremiumVanish share API usage

    PREMIUMVANISH("PremiumVanish", "https://www.spigotmc.org/data/resource_icons/14/14404.jpg?1447087910"),
    SUPERVANISH("SuperVanish", "https://www.spigotmc.org/data/resource_icons/1/1331.jpg?141236592"),
    VANISHNOPACKET("VanishNoPacket", "https://media.forgecdn.net/avatars/65/903/636163044899797953.png"),
    @Deprecated EMPTY("null", "null"),
    IDISGUISE("iDisguise", "https://www.spigotmc.org/data/resource_icons/5/5509.jpg?1483142473"),
    ESSENTIALSX("Essentials", "https://www.spigotmc.org/data/resource_icons/9/9089.jpg?1468342131");

    public final Pair<String, String> pair;

    Dependency(@NonNull String name, @NonNull String icon) {
        this.pair = new Pair<>(name, icon);
    }

    public String getName() {
        return this.pair.getLeft();
    }

    public String getIcon() {
        return this.pair.getRight();
    }

}
