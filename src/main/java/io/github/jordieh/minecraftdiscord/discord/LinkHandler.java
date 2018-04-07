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

import io.github.jordieh.minecraftdiscord.configuration.PluginConfiguration;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LinkHandler {

    private final Logger logger = LoggerFactory.getLogger(LinkHandler.class);

    private static LinkHandler instance;

    private final PluginConfiguration configuration;
    private final Map<Integer, UUID> uuidMap;
    private final Map<Long, UUID> linkMap;
    private final String path;


    private LinkHandler() {
        logger.debug("Constructing LinkHandler");

        this.path = "accounts";
        this.configuration = new PluginConfiguration(this.path);

        this.uuidMap = new HashMap<>();
        this.linkMap = configuration.getConfig().getDefaultSection()
                .getValues(false)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> Long.valueOf(e.getKey()), e -> UUID.fromString((String) e.getValue())));
    }

    public static LinkHandler getInstance() {
        return instance == null ? instance = new LinkHandler() : instance;
    }

    public void saveResources() {
        Map<Long, String> stringMap = this.linkMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));

        this.configuration.getConfig().set(this.path, stringMap);
        this.configuration.saveConfig();
    }

    public boolean isLinked(@NonNull UUID uuid) {
        return this.linkMap.containsValue(uuid);
    }

    public boolean isLinked(@NonNull IUser user) {
        return this.linkMap.containsKey(user.getLongID());
    }

    @Deprecated
    public boolean isLinked(@NonNull long id) {
        return this.linkMap.containsKey(id);
    }

    public String getUserUUIDString(@NonNull IUser user) {
        try {
            return linkMap.get(user.getLongID()).toString();
        } catch (NullPointerException e) {
            return user.getName();
        }
    }

    @Deprecated
    public UUID getUserUUID(@NonNull IUser user) {
        try {
            return linkMap.get(user.getLongID());
        } catch (NullPointerException e) {
            return UUID.randomUUID();
        }
    }

    public Optional<Map.Entry<Long, UUID>> getLinkedUser(@NonNull UUID uuid) {
        return this.linkMap.entrySet().stream()
                .filter(e -> e.getValue().equals(uuid))
                .findFirst();
    }

    public Optional<UUID> linkAccount(@NonNull IUser user, @NonNull int code) {
        if (!this.uuidMap.containsKey(code)) {
            return Optional.empty();
        }

        UUID uuid = this.uuidMap.get(code);
        this.linkMap.put(user.getLongID(), uuid);
        this.uuidMap.remove(code);
        return Optional.of(uuid);
    }

    public int generateCode(@NonNull UUID uuid) {
        String s = uuid.toString();

        if (this.uuidMap.containsValue(uuid)) {
            return uuidMap.entrySet().stream()
                    .filter(e -> e.getValue().equals(uuid))
                    .findFirst().orElseThrow(() -> new NullPointerException("This should never happen: " + s))
                    .getKey();
        }

        int x;
        while (true) {
            x = this.generateCode();
            System.out.println("Generated code " + x);
            if (!this.uuidMap.containsKey(x)) {
                this.uuidMap.put(x, uuid);
                return x;
            }
        }
    }

    private int generateCode() {
        return 100000 + ((int) (ThreadLocalRandom.current().nextFloat() * 900000.0f));
    }

    public Optional<UUID> unlink(@NonNull UUID uuid) {
        Optional<Map.Entry<Long, UUID>> optional = this.getLinkedUser(uuid);
        if (optional.isPresent()) {
            this.linkMap.remove(optional.get().getKey());
            return Optional.of(optional.get().getValue());
        }
        return Optional.empty();
    }

    public Optional<UUID> unlink(@NonNull long id) {
        if (!this.linkMap.containsKey(id)) {
            UUID temp = this.linkMap.get(id);
            linkMap.remove(id);
            return Optional.of(temp);
        }
        return Optional.empty();
    }

}
