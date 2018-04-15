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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import sx.blah.discord.handle.obj.IChannel;

public class DiscordAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent eventObject) {
        ConsoleWorker.queue.add(eventObject.getMessage());
//        if (!MinecraftDiscord.started) {
//            MinecraftDiscord.queue.offer(eventObject.getMessage());
//            return;
//        }
//        Optional<IChannel> channel = ChannelHandler.getInstance().getConnectedChannel("console");
//        channel.ifPresent(channel1 -> ClientHandler.getInstance().sendMessage(channel1, eventObject.getMessage()));
    }

    public static void process() {
        if (MinecraftDiscord.started) {
            return;
        }
        MinecraftDiscord.started = true;
        IChannel channel = ClientHandler.getInstance().getClient()
                .getChannels().stream()
                .filter(c -> c.getName().equalsIgnoreCase("console"))
                .findFirst().orElse(null);
        if (channel == null) {
            return;
        }

        new Thread(() -> {
            while (!MinecraftDiscord.queue.isEmpty()) {
                String s = MinecraftDiscord.queue.poll();
                ClientHandler.getInstance().sendMessage(channel, s);
            }
        }, "ConsoleQueueWorker").start();
    }
}
