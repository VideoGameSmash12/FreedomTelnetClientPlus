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
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Player
{
    @Expose
    @SerializedName("tfm.admin.isAdmin")
    private String isAdmin;

    @Expose
    @SerializedName("tfm.admin.isTelnetAdmin")
    private String isTelnet;

    @Expose
    @SerializedName("tfm.admin.isSeniorAdmin")
    private String isSenior;

    @Expose
    @SerializedName("tfm.essentialsBridge.getNickname")
    private String nickname;

    @Expose
    @SerializedName("tfm.playerdata.getTag")
    private String tag;

    @Expose
    private String uuid;

    @Expose
    private String displayName;

    @Expose
    private String name;

    @Expose
    private String ip;
}
