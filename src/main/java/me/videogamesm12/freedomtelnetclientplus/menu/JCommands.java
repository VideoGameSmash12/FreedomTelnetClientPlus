/*
 * Copyright (C) 2022 Video
 *
 * This file is part of FreedomTelnetClient+.
 *
 * FreedomTelnetClient+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.videogamesm12.freedomtelnetclientplus.menu;

import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import me.StevenLawson.BukkitTelnetClient.BukkitTelnetClient;
import me.videogamesm12.freedomtelnetclientplus.config.Config;
import me.videogamesm12.freedomtelnetclientplus.data.ServerCommand;

public class JCommands extends JMenu
{
    public JCommands()
    {
        final ActionListener actionListener = event ->
        {
            if (BukkitTelnetClient.mainPanel != null)
            {
                BukkitTelnetClient.mainPanel.getConnectionManager().sendDelayedCommand(event.getActionCommand(), true, 100);
            }
        };
        
        for (ServerCommand commandData : Config.instance.getServerCommands())
        {
            final JMenuItem item = new JMenuItem();
            item.setText(commandData.getName());
            item.setActionCommand(commandData.getCommand());
            item.addActionListener(actionListener);

            add(item);
        }
    }
}
