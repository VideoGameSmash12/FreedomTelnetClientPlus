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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Data;
import me.StevenLawson.BukkitTelnetClient.BukkitTelnetClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Enhancements
{
    private static final Pattern enhancementRegex = Pattern.compile(":\\[.+@BukkitTelnet]\\$ ([A-z]+)~(.+)");
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Data
    public static class EnhancedPlusModeDataset
    {
        @Expose
        private String tps;
    }

    @Data
    public static class EnhancedModeDataset
    {
        @Expose
        private List<Player> players = new ArrayList<>();
    }

    public static void processDataset(String message)
    {
        Matcher result = enhancementRegex.matcher(message);

        if (!result.find())
        {
            return;
        }

        switch (result.group(1))
        {
            // Enhanced mode dataset
            case "playerList":
            {
                BukkitTelnetClient.mainPanel.updatePlayerList(gson.fromJson(result.group(2), EnhancedModeDataset.class));
                break;
            }

            // Enhanced+ mode dataset
            case "usage":
            {
                BukkitTelnetClient.mainPanel.updateTPSCounter(gson.fromJson(result.group(2), EnhancedPlusModeDataset.class).getTps());
                break;
            }
        }
    }

    public static boolean isDataset(String message)
    {
        return enhancementRegex.matcher(message).find();
    }

}
