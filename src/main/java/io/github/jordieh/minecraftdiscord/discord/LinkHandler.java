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

package io.github.jordieh.minecraftdiscord.discord;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LinkHandler {

    private final Logger logger = LoggerFactory.getLogger(LinkHandler.class);

    private static LinkHandler instance;

    private Map<Integer, UUID> uuidMap;
    @Getter private Map<Long, UUID> linkMap;

    private LinkHandler() {
        logger.debug("Constructing LinkHandler");
        linkMap = new HashMap<>();
        uuidMap = new HashMap<>();
    }

    public static LinkHandler getInstance() {
        return instance == null ? instance = new LinkHandler() : instance;
    }

    public boolean isLinked(UUID uuid) {
        return linkMap.values().contains(uuid);
    }

    public long getLinkedUser(UUID uuid) {
        return linkMap.entrySet().stream()
                .filter(e -> e.getValue().equals(uuid))
                .findFirst().orElseThrow(() -> new NullPointerException("This should never happen?"))
                .getKey();
    }

    public boolean linkAccount(IUser user, int code) {
        logger.debug("Trying to link user {} with a Minecraft account using code {}", user.getLongID(), code);
        if (!uuidMap.containsKey(code)) {
            logger.trace("Linking user {} has failed (Incorrect code)", user.getLongID());
            return false;
        }
        UUID uuid = uuidMap.get(code);
        linkMap.put(user.getLongID(), uuid);
        uuidMap.remove(code);
        logger.debug("Succesfully linked user M:{} to D:{} using code {}", uuid.toString(), user.getLongID(), code);
        return true;
    }

    public int generateCode(UUID uuid) {
        String uuidString = uuid.toString();
        logger.debug("Generating code for {}", uuidString);
        if (uuidMap.containsValue(uuid)) {
            logger.trace("Linking system already had code for {} generated", uuidString);
            return uuidMap.entrySet().stream()
                    .filter(e -> e.getValue().equals(uuid))
                    .findFirst().orElseThrow(() -> new NullPointerException("This should never happen?"))
                    .getKey();
        }
        int x;
        while (true) {
            x = generateCode();
            if (!uuidMap.containsKey(x)) {
                logger.debug("Generated code {} for {}", x, uuidString);
                uuidMap.put(x, uuid);
                break;
            }
            logger.trace("Generated duplicate code for {} [{}]", uuidString, x);
        }
        return x;
    }

    private int generateCode() {
        return 100000 + ((int) (ThreadLocalRandom.current().nextFloat() * 900000.0f));
    }
}
