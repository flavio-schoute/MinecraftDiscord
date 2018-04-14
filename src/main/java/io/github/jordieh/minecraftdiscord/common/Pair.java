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

package io.github.jordieh.minecraftdiscord.common;

import lombok.Getter;

import java.util.Objects;

public class Pair<L, R> {

    @Getter private final L left;
    @Getter private final R right;

    @Getter private final boolean isEmpty;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
        this.isEmpty = (left == null) && (right == null);
    }

    public Pair() {
        this(null, null);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(left) ^ Objects.hashCode(right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Pair) {
            Pair pair = (Pair) obj;
            return Objects.equals(this.left, pair.getLeft()) && Objects.equals(this.right, pair.getRight());
        }
        return false;
    }
}
