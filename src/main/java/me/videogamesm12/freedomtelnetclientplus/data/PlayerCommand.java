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
import lombok.Builder;
import lombok.Data;
import me.StevenLawson.BukkitTelnetClient.BukkitTelnetClient;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

@Builder
@Data
public class PlayerCommand
{
    @Expose
    private String name;

    @Expose
    private String command;

    public boolean hasReason()
    {
        return getCommand().contains("$REASON");
    }

    public String getProcessed(Player player)
    {
        String[] from = {"$TARGET_NAME", "$TARGET_IP", "$TARGET_UUID", "$REASON"};
        String[] to = {player.getName(), player.getIp(), player.getUuid(), (hasReason() ? JOptionPane.showInputDialog(BukkitTelnetClient.mainPanel, "Please specify a reason.", JOptionPane.PLAIN_MESSAGE) : null)};

        return StringUtils.replaceEach(getCommand(), from, to);
    }
}
