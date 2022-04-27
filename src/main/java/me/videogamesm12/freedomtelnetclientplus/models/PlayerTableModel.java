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
package me.videogamesm12.freedomtelnetclientplus.models;

import me.videogamesm12.freedomtelnetclientplus.Util;
import me.videogamesm12.freedomtelnetclientplus.data.Player;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PlayerTableModel extends AbstractTableModel
{
    private final String[] columns = {"Name", "IP", "Tag", "Nickname", "Rank"};
    private final List<Player> rows = new ArrayList<>();

    @Override
    public int getRowCount()
    {
        return rows.size();
    }

    @Override
    public int getColumnCount()
    {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        return columns[columnIndex];
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex)
    {
        Player player = rows.get(rowIndex);

        switch (columnIndex)
        {
            // Name
            case 0:
                return player.getName();

            // IP Address
            case 1:
                return player.getIp();

            // Tag
            case 2:
                return player.getTag().equals("null") ? null : player.getTag();

            // Nickname
            case 3:
                return player.getNickname().equals("null") ? null : player.getNickname();

            // Rank
            case 4:
                return Util.getRank(player);

            // Return null otherwise
            default:
                return null;
        }
    }

    public Player getPlayer(int row)
    {
        return rows.size() < row ? null : rows.get(row);
    }

    public void update(List<Player> playerList)
    {
        rows.clear();
        rows.addAll(playerList);

        fireTableDataChanged();
    }
}
