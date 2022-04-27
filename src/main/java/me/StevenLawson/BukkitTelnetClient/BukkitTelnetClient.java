/*
 * Copyright (C) 2012-2017 Steven Lawson
 * Copyright (C) 2021-2022 Video
 *
 * This file is part of FreedomTelnetClient.
 *
 * FreedomTelnetClient is free software: you can redistribute it and/or modify
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
package me.StevenLawson.BukkitTelnetClient;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import me.videogamesm12.freedomtelnetclientplus.config.Config;
import me.videogamesm12.freedomtelnetclientplus.data.Theme;

public class BukkitTelnetClient
{
    public static final String VERSION_STRING = getVersionString();
    public static final String[] DEVELOPERS = {"videogamesm12", "StevenLawson"};
    public static final Logger LOGGER = Logger.getLogger(BukkitTelnetClient.class.getName());
    public static BTC_MainPanel mainPanel = null;

    public static void main(String[] args)
    {
        changeTheme(Config.instance.getPreferences().getCurrentTheme(), false);
        
        SwingUtilities.invokeLater(() ->
        {
            mainPanel = new BTC_MainPanel();
            mainPanel.setup();
        });
    }

    public static void changeTheme(Theme theme, boolean update)
    {
        if (theme != null)
        {
            theme.apply();
            Config.instance.getPreferences().setCurrentTheme(theme);
            Config.save();
        }
        else
        {
            // Fallback, in case something isn't right
            findAndSetLookAndFeel("Windows");
        }

        if (update)
        {
            SwingUtilities.updateComponentTreeUI(mainPanel);
            mainPanel.pack();
            mainPanel.sizeManagement();
        }
    }
    
    public static void findAndSetLookAndFeel(final String searchStyleName)
    {
        try
        {
            javax.swing.UIManager.LookAndFeelInfo foundStyle = null;
            javax.swing.UIManager.LookAndFeelInfo fallbackStyle = null;

            for (javax.swing.UIManager.LookAndFeelInfo style : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if (searchStyleName.equalsIgnoreCase(style.getName()))
                {
                    foundStyle = style;
                    break;
                }
                else if ("Nimbus".equalsIgnoreCase(style.getName()))
                {
                    fallbackStyle = style;
                }
            }

            if (foundStyle != null)
            {
                javax.swing.UIManager.setLookAndFeel(foundStyle.getClassName());
            }
            else if (fallbackStyle != null)
            {
                javax.swing.UIManager.setLookAndFeel(fallbackStyle.getClassName());
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex)
        {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public static String getVersionString()
    {
        try (final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("my.properties"))
        {
            final Properties properties = new Properties();
            properties.load(inputStream);
            return String.format("v%s", properties.getProperty("version"));
        }
        catch (Exception ex)
        {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return "Unknown";
    }
    
    public static String getBuildDate()
    {
        try (final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("my.properties"))
        {
            final Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("timestampDate");
        }
        catch (Exception ex)
        {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return "Unknown";
    }
    
    public static void quit()
    {
        Config.save();
        System.exit(0);
    }
}
