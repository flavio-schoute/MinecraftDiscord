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

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.discord.command.CommandExecutor;
import io.github.jordieh.minecraftdiscord.discord.command.InfoCommand;
import io.github.jordieh.minecraftdiscord.discord.command.LinkCommand;
import io.github.jordieh.minecraftdiscord.discord.command.UnlinkCommand;
import org.bukkit.configuration.file.FileConfiguration;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandHandler {

    private static CommandHandler instance;

    private final Map<String, CommandExecutor> executorMap;
    private final Map<String, String> stringMap;
    private final String prefix;

    private CommandHandler() {
        this.executorMap = new HashMap<>();
        this.executorMap.put("link", new LinkCommand());
        this.executorMap.put("unlink", new UnlinkCommand());
        this.executorMap.put("minecraftdiscord", new InfoCommand());

        FileConfiguration configuration = MinecraftDiscord.getInstance().getConfig();
        this.prefix = configuration.getString("options.prefix");

        this.stringMap = configuration.getConfigurationSection("command-execution").getValues(false)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
    }

    public static CommandHandler getInstance() {
        return instance == null ? instance = new CommandHandler() : instance;
    }

    public boolean handleExecution(MessageReceivedEvent event) {
        String content = event.getMessage().getContent();

        if (!content.startsWith(this.prefix)) {
            return false;
        }

        String[] args = event.getMessage().getContent().split(" ");

        String command = args[0];

        if (this.executorMap.containsKey(command)) {
            this.executorMap.get(command).execute(event, event.getChannel(), event.getMessage(), event.getAuthor(), args);
            return true;
        }

        if (this.stringMap.containsKey(command)) {
            ClientHandler.getInstance().deleteMessage(event.getMessage());
            ClientHandler.getInstance().sendMessage(event.getChannel(), this.stringMap.get(command));
            return true;
        }

        return false;
    }

}
