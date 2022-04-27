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
package me.videogamesm12.freedomtelnetclientplus.data;

import com.google.gson.annotations.Expose;
import lombok.Data;
import me.StevenLawson.BukkitTelnetClient.BukkitTelnetClient;
import me.videogamesm12.freedomtelnetclientplus.config.Config;

import java.awt.*;
import java.util.Arrays;
import java.util.regex.Pattern;

@Data
public class Filter
{
    @Expose
    private String name = "Untitled";
    @Expose
    private String regex;
    //--
    @Expose
    private int[] color = null;
    //--
    @Expose
    private boolean ignored;
    //--
    private Pattern pattern = null;

    public Filter(String name, String regex, int[] color, boolean ignored)
    {
        this.name = name;
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
        this.color = color;
        this.ignored = ignored;
    }

    /**
     * Filters the contents of a message, applying any additional information along the way.
     *
     * @param input Message
     * @return Message
     */
    public Message filter(Message input)
    {
        // UGLY FUCKING HACK HOLY MOTHER OF GOD
        if (pattern == null)
        {
            pattern = Pattern.compile(regex);
        }

        // If the pattern doesn't match, just return the original input.
        if (!pattern.matcher(input.getMessage()).find())
        {
            return input;
        }

        // If we're supposed to ignore it, then return null.
        if (ignored)
            return null;

        // Apply a color if one is specified.
        if (color != null && color.length == 3)
        {
            input.setColor(new Color(color[0], color[1], color[2]));
        }
        else
        {
            BukkitTelnetClient.LOGGER.info(Arrays.toString(color));
        }

        // Return the modified message.
        return input;
    }

    public static Message getFilteredMessage(Message original)
    {
        for (Filter filter : Config.instance.getFilters())
        {
            original = filter.filter(original);

            if (original == null)
            {
                break;
            }
        }

        return original;
    }
}
