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
package me.videogamesm12.freedomtelnetclientplus;

import me.videogamesm12.freedomtelnetclientplus.config.Config;
import me.videogamesm12.freedomtelnetclientplus.data.Player;

public class Util
{
    public static String getRank(Player player)
    {
        Boolean isAdmin = Boolean.parseBoolean(player.getIsAdmin());
        Boolean isTelnet = Boolean.parseBoolean(player.getIsTelnet());
        boolean isSenior = Boolean.parseBoolean(player.getIsSenior());

        if (!Config.instance.getPreferences().isUsingLegacyRanks())
        {
            if (isAdmin && isTelnet)
                return "Admin";
            else if (isSenior)
                return "Senior";
            else
                return null;
        }
        else
        {
            if (isAdmin)
                return "Super";
            else if (isTelnet)
                return "Telnet";
            else if (isSenior)
                return "Senior";
            else
                return null;
        }
    }
}
