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

import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme;
import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import me.StevenLawson.BukkitTelnetClient.BukkitTelnetClient;
import org.netbeans.swing.laf.dark.DarkMetalLookAndFeel;
import org.netbeans.swing.laf.dark.DarkNimbusLookAndFeel;

public enum Theme
{
    FLATLAF_DARK_PURPLE("FlatLAF - Dark Purple", ThemeType.FLATLAF, FlatDarkPurpleIJTheme.class),
    FLATLAF_HIGH_CONTRAST("FlatLAF - High Contrast", ThemeType.FLATLAF, FlatHighContrastIJTheme.class),
    FLATLAF_MATERIAL_DARKER("FlatLAF - Material Darker", ThemeType.FLATLAF, FlatMaterialDarkerIJTheme.class),
    FLATLAF_MATERIAL_LIGHTER("FlatLAF - Material Lighter", ThemeType.FLATLAF, FlatMaterialLighterIJTheme.class),
    FLATLAF_MOONLIGHT("FlatLAF - Moonlight", ThemeType.FLATLAF, FlatMoonlightIJTheme.class),
    FLATLAF_PALENIGHT("FlatLAF - Palenight", ThemeType.FLATLAF, FlatMaterialPalenightIJTheme.class),
    FLATLAF_SOLARIZED_DARK("FlatLAF - Solarized Dark", ThemeType.FLATLAF, FlatSolarizedDarkIJTheme.class),
    FLATLAF_SOLARIZED_LIGHT("FlatLAF - Solarized Light", ThemeType.FLATLAF, FlatSolarizedLightIJTheme.class),
    //--
    METAL("Metal", ThemeType.LEGACY, MetalLookAndFeel.class.getName()),
    NATIVE("Native", ThemeType.LEGACY, UIManager.getSystemLookAndFeelClassName()),
    NETBEANS_DARK_METAL("NetBeans - Dark Metal", ThemeType.LEGACY, DarkMetalLookAndFeel.class.getName()),
    NETBEANS_DARK_NIMBUS("NetBeans - Dark Nimbus", ThemeType.LEGACY, DarkNimbusLookAndFeel.class.getName()),
    NIMBUS("Nimbus", ThemeType.LEGACY, NimbusLookAndFeel.class.getName()),
    WINDOWS("Windows", ThemeType.LEGACY, WindowsLookAndFeel.class.getName()),
    WINDOWS_CLASSIC("Windows Classic", ThemeType.LEGACY, WindowsClassicLookAndFeel.class.getName());

    public final String displayName;
    public ThemeType type;
    public Object theme;

    Theme(String displayName, ThemeType type, Object theme)
    {
        this.displayName = displayName;
        this.type = type;
        this.theme = theme;
    }

    public void apply()
    {
        switch (type)
        {
            case FLATLAF:
            {
                try
                {
                    Class<? extends com.formdev.flatlaf.IntelliJTheme.ThemeLaf> flatlafTheme = (Class<? extends com.formdev.flatlaf.IntelliJTheme.ThemeLaf>) theme;
                    flatlafTheme.getMethod("setup").invoke(null);
                }
                catch (Exception ex)
                {
                    BukkitTelnetClient.LOGGER.warning("Failed to load FlatLaf themes");
                    ex.printStackTrace();
                }

                return;
            }

            case METAL:
            {
                try
                {
                    UIManager.setLookAndFeel(new MetalLookAndFeel());
                    MetalTheme metalTheme = (MetalTheme) theme;
                    MetalLookAndFeel.setCurrentTheme(metalTheme);
                }
                catch (Exception ex)
                {
                    BukkitTelnetClient.LOGGER.warning("Failed to load Metal themes");
                    ex.printStackTrace();
                }

                return;
            }

            case LEGACY:
            {
                String path = (String) theme;
                try
                {
                    UIManager.setLookAndFeel(path);
                }
                catch (Exception ex)
                {
                    BukkitTelnetClient.LOGGER.warning("Failed to load legacy themes");
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return this.displayName;
    }

    public enum ThemeType
    {
        FLATLAF,
        METAL,
        LEGACY
    }
}
