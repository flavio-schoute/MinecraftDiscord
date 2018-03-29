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

package io.github.jordieh.minecraftdiscord;

import io.github.jordieh.minecraftdiscord.discord.ClientHandler;

import java.awt.*;

@Deprecated
public class MainRunnerTest extends Frame {

    public MainRunnerTest() throws HeadlessException {// Panel is a container
        Button btn = new Button("Exit Discord"); // Button is a component
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
        ClientHandler.getInstance();

        new MainRunnerTest();
    }
}
