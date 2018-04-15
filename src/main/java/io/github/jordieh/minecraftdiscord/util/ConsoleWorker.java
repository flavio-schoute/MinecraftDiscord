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

package io.github.jordieh.minecraftdiscord.util;

import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import io.github.jordieh.minecraftdiscord.world.ChannelHandler;
import sx.blah.discord.handle.obj.IChannel;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public final class ConsoleWorker extends Thread {

    public static final Queue<String> queue = new LinkedBlockingDeque<>();
    private static final String seperator = System.lineSeparator();

    public ConsoleWorker() {
        super("ConsoleWorker");
    }

    @Override
    public void run() {
        while (true) {
            Optional<IChannel> channel = ChannelHandler.getInstance().getConsoleChannel();
            if (!channel.isPresent()) {
                continue;
            }

            StringBuilder builder = new StringBuilder();
            String input = queue.poll();

            while (input != null) {
                if (builder.length() + input.length() >= 2000) {
                    ClientHandler.getInstance().sendMessage(channel.get(), builder.toString());
                    builder = new StringBuilder();
                }

                builder.append(input);
                builder.append(seperator);

                input = queue.poll();
            }

            String s = builder.toString();
            if (!s.replace(seperator, "").isEmpty()) {
                ClientHandler.getInstance().sendMessage(channel.get(), s);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
