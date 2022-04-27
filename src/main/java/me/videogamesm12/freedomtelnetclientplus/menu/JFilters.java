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

import me.StevenLawson.BukkitTelnetClient.BukkitTelnetClient;
import me.videogamesm12.freedomtelnetclientplus.config.Config;

import javax.swing.*;
import java.awt.*;

public class JFilters extends JMenu
{
    public JFilters()
    {
        super("Filters");

        updateMenu();
    }

    public void updateMenu()
    {
        removeAll();
        Config.instance.getFilters().forEach((filter) -> {
            JMenu item = new JMenu(filter.getName());
            JCheckBoxMenuItem ignored = new JCheckBoxMenuItem("Hidden", filter.isIgnored());
            ignored.addActionListener(e -> filter.setIgnored(item.isSelected()));
            JMenuItem color = new JMenuItem("Change color");
            color.addActionListener(e -> {
                Color colour = JColorChooser.showDialog(BukkitTelnetClient.mainPanel, "Change text color", filter.getColor() != null ? new Color(filter.getColor()[0], filter.getColor()[1], filter.getColor()[2]) : null);
                filter.setColor(new int[]{colour.getRed(), colour.getGreen(), colour.getBlue()});
                Config.save();
            });
            item.add(ignored);
            item.add(color);
            add(item);
        });
    }
}
