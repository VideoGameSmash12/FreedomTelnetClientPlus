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
package me.videogamesm12.freedomtelnetclientplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Data;
import me.StevenLawson.BukkitTelnetClient.BukkitTelnetClient;
import me.videogamesm12.freedomtelnetclientplus.data.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Config
{
    private static final String defaultPrefix = "^:\\[.+? INFO\\]: ";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    public static Config instance;

    @Expose
    private Preferences preferences = new Preferences();

    @Expose
    private List<Filter> filters = new ArrayList<>(Arrays.asList(
            new Filter("Warnings, errors, and fatal errors", "^:\\[.+? (?:(WARN)|(ERROR)|(FATAL))\\]: ", null, false),
            new Filter("Admin chat messages", defaultPrefix + "\\[ADMIN\\] ", new int[]{189, 147, 249}, false),
            new Filter("Discord messages", defaultPrefix + "\\[Discord\\] .+?:", new int[]{83, 148, 236}, false),
            new Filter("Server chat messages", defaultPrefix + ".+? (?:(»)|(\\?))", new int[]{83, 148, 236}, false),
            new Filter("Preprocess command logs", defaultPrefix + "\\[PREPROCESS_COMMAND\\] ", new int[]{129, 199, 132}, true),
            new Filter("FreedomTelnetClient+ messages", "\\[FTC\\+\\] ", new int[]{129, 199, 132}, false)
    ));

    @Expose
    private List<Server> servers = new ArrayList<>(Arrays.asList(
            Server.builder().name("Localhost - BukkitTelnet default").ip("localhost:8765").active(true).build(),
            Server.builder().name("Localhost - Protocol default").ip("localhost:23").active(true).build()
    ));

    @Expose
    private List<ServerCommand> serverCommands = new ArrayList<>(Arrays.asList(
            ServerCommand.builder().name("OP Everyone").command("opall").build(),
            ServerCommand.builder().name("De-OP Everyone").command("deopall").build(),
            ServerCommand.builder().name("Clean Nicknames").command("nickclean").build(),
            ServerCommand.builder().name("Enable Adminmode").command("adminmode on").build(),
            ServerCommand.builder().name("Disable Adminmode").command("adminmode off").build(),
            ServerCommand.builder().name("Give Everyone Cake").command("cake").build(),
            ServerCommand.builder().name("Give Everyone Cookies").command("cookie").build(),
            ServerCommand.builder().name("Purge Mobs").command("mp").build(),
            ServerCommand.builder().name("Wipe Entities").command("rd").build(),
            ServerCommand.builder().name("Purge Various Data").command("purgeall").build(),
            ServerCommand.builder().name("Reset WorldEdit Limits").command("setl").build(),
            ServerCommand.builder().name("Stop Server").command("stop").build(),
            ServerCommand.builder().name("Toggle Water Placement").command("toggle waterplace").build(),
            ServerCommand.builder().name("Toggle Fire Placement").command("toggle fireplace").build(),
            ServerCommand.builder().name("Toggle Lava Placement").command("toggle lavaplace").build(),
            ServerCommand.builder().name("Toggle Fluid Spread").command("toggle fluidspread").build(),
            ServerCommand.builder().name("Toggle Fire Spread").command("toggle firespread").build(),
            ServerCommand.builder().name("Toggle Lava Damage").command("toggle lavadmg").build(),
            ServerCommand.builder().name("Toggle Lockdown").command("toggle lockdown").build(),
            ServerCommand.builder().name("Toggle Block Gravity").command("toggle gravity").build(),
            ServerCommand.builder().name("Toggle Explosives").command("toggle explosives").build()
    ));

    @Expose
    private List<PlayerCommand> playerCommands = new ArrayList<>(Arrays.asList(
            PlayerCommand.builder().name("Ban").command("ban $TARGET_NAME $REASON").build(),
            PlayerCommand.builder().name("Ban IP").command("banip $TARGET_NAME $REASON").build(),
            PlayerCommand.builder().name("Kick").command("kick $TARGET_NAME $REASON").build(),
            PlayerCommand.builder().name("Mute").command("mute $TARGET_NAME").build(),
            PlayerCommand.builder().name("Unmute").command("unmute $TARGET_NAME").build(),
            PlayerCommand.builder().name("5 Minute Ban").command("tempban $TARGET_NAME 5m $REASON").build(),
            PlayerCommand.builder().name("Smite").command("smite $TARGET_NAME $REASON").build(),
            PlayerCommand.builder().name("Warn").command("warn $TARGET_NAME $REASON").build(),
            PlayerCommand.builder().name("OP").command("op $TARGET_NAME").build(),
            PlayerCommand.builder().name("De-OP").command("deop $TARGET_NAME").build(),
            PlayerCommand.builder().name("Freeze").command("fr $TARGET_NAME").build(),
            PlayerCommand.builder().name("Cage").command("cage $TARGET_NAME").build(),
            PlayerCommand.builder().name("Uncage").command("uncage $TARGET_NAME").build(),
            PlayerCommand.builder().name("Doom").command("doom $TARGET_NAME").build(),
            PlayerCommand.builder().name("Seen").command("seen $TARGET_UUID").build(),
            PlayerCommand.builder().name("Seen IP").command("seen $TARGET_IP").build(),
            PlayerCommand.builder().name("Seen Name").command("seen $TARGET_NAME").build(),
            PlayerCommand.builder().name("Name History").command("nh $TARGET_NAME").build()
    ));

    static
    {
        load();
    }

    @Data
    public static class Preferences
    {
        @Expose
        private boolean autoScroll = true;

        @Expose
        private boolean autoEnhanced = true;

        @Expose
        private boolean autoEnhancedPlus = true;

        @Expose
        private boolean usingLegacyRanks;

        @Expose
        private Theme currentTheme = Theme.FLATLAF_MATERIAL_DARKER;
    }

    public static void load()
    {
        try
        {
            FileReader reader = new FileReader("ftcp-settings.json");
            instance = gson.fromJson(reader, Config.class);
        }
        catch (Exception ex)
        {
            BukkitTelnetClient.LOGGER.severe("Failed to load configuration");
            ex.printStackTrace();
            //--
            instance = new Config();
        }
    }

    public static void save()
    {
        try
        {
            FileWriter writer = new FileWriter("ftcp-settings.json");
            writer.write(gson.toJson(instance));
            writer.close();
        }
        catch (Exception ex)
        {
            BukkitTelnetClient.LOGGER.severe("Failed to save configuration");
            ex.printStackTrace();
        }
    }
}
