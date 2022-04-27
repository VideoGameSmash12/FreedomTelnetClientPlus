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
import me.videogamesm12.freedomtelnetclientplus.data.Theme;

import javax.swing.*;
import java.util.Arrays;

public class JThemes extends JMenu
{
    private final ButtonGroup group = new ButtonGroup();

    public JThemes()
    {
        super("Themes");

        Arrays.stream(Theme.values()).forEach((theme) -> {
            JRadioButtonMenuItem radio = new JRadioButtonMenuItem(theme.displayName);
            if (Config.instance.getPreferences().getCurrentTheme() == theme)
                radio.setSelected(true);

            radio.addActionListener((e) -> BukkitTelnetClient.changeTheme(theme, true));

            group.add(radio);
            add(radio);
        });
    }
}
