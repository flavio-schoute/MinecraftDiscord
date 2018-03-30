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

package io.github.jordieh.minecraftdiscord.testing;

import io.github.jordieh.minecraftdiscord.discord.ClientHandler;
import sx.blah.discord.util.DiscordException;

import java.awt.*;

@Deprecated
public class MainRunnerTest extends Frame {

    public MainRunnerTest() throws HeadlessException {
        Button btn = new Button("Exit Discord");
        setTitle("Discord control");
        Label label = new Label("Button for exiting Discord");
        setLayout(new FlowLayout());
        add(label);
        add(btn);
        setResizable(false);
        btn.addActionListener((a) -> {
            ClientHandler.getInstance().getClient().logout();
            System.exit(0);
        });
        setVisible(true);
        setSize(400, 100);
    }

    public static void main(String[] args) {
//        org.slf4j.Logger logger = LoggerFactory.getLogger(MainRunnerTest.class);
        try {
            ClientHandler.getInstance();
            new MainRunnerTest();
        } catch (DiscordException e) {
//            logger.error("Detected client failure at startup", e);
            System.exit(0);
            // @TODO: Disable plugin, discord connection not available
        }
    }
}
