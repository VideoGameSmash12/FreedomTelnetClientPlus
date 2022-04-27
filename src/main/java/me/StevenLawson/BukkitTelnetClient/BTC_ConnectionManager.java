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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Timer;
import me.videogamesm12.freedomtelnetclientplus.config.Config;
import me.videogamesm12.freedomtelnetclientplus.data.Enhancements;
import me.videogamesm12.freedomtelnetclientplus.data.Message;
import me.videogamesm12.freedomtelnetclientplus.data.Server;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.net.telnet.TelnetClient;

public class BTC_ConnectionManager
{
    private static final Pattern LOGIN_MESSAGE = Pattern.compile("\\[.+?@BukkitTelnet]\\$ Logged in as (.+)\\.");

    private final TelnetClient telnetClient = new TelnetClient();
    private Thread connectThread;
    private String hostname;
    private int port;
    private boolean canDoDisconnect = false;
    private String loginName;

    public BTC_ConnectionManager()
    {
    }

    public void triggerConnect(final String hostname, final int port)
    {
        final BTC_MainPanel btc = BukkitTelnetClient.mainPanel;

        btc.getBtnConnect().setText("Disconnect");
        btc.getTxtServer().setEnabled(false);

        btc.writeToConsole(new Message(String.format("Connecting to %s:%s...", hostname, port), true));

        this.hostname = hostname;
        this.port = port;
        this.loginName = null;
        updateTitle(true);

        startConnectThread();
    }

    public void triggerConnect(final String hostnameAndPort)
    {
        final String[] parts = StringUtils.split(hostnameAndPort, ":");

        if (parts.length <= 1)
        {
            this.triggerConnect(parts[0], 23);
        }
        else
        {
            int _port = 23;

            try
            {
                _port = Integer.parseInt(parts[1]);
            }
            catch (NumberFormatException ignored)
            {
            }

            this.triggerConnect(parts[0], _port);
        }
    }

    public void triggerDisconnect()
    {
        if (this.canDoDisconnect)
        {
            this.canDoDisconnect = false;

            try
            {
                this.telnetClient.disconnect();
            }
            catch (IOException ex)
            {
                BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void finishDisconnect()
    {
        final BTC_MainPanel btc = BukkitTelnetClient.mainPanel;

        btc.getBtnConnect().setText("Connect");
        btc.getTxtServer().setEnabled(true);
        btc.getBtnSend().setEnabled(false);
        btc.getTxtCommand().setEnabled(false);

        loginName = null;

        btc.updateTPSCounter(null);
        btc.getTxtNumPlayers().setText("Not connected");
        updateTitle(false);

        btc.writeToConsole(new Message("Disconnected.", true));
    }

    public void sendCommand(final String text)
    {
        sendCommand(text, true);
    }

    public void sendCommand(String text, final boolean verbose)
    {
        if (text.length() > 32767)
        {
            text = text.substring(0, 32766);
            BukkitTelnetClient.mainPanel.writeToConsole(new Message("Your command was shortened because it was too long to send.", true));
        }
        
        if (text.startsWith("ftcp."))
        {
            String[] commands = text.split("\\.");
            String[] args = text.split(" ");
            switch(commands[1].toLowerCase())
            {
                case "disconnect":
                {
                    triggerDisconnect();
                    return;
                }
                
                case "save":
                {
                    BukkitTelnetClient.mainPanel.writeToConsole(new Message("Attempting to save the current configuration to disk...", true));
                    try
                    {
                        Config.save();
                        BukkitTelnetClient.mainPanel.writeToConsole(new Message("Configuration saved.", true));
                    }
                    catch (Exception ex)
                    {
                        BukkitTelnetClient.mainPanel.writeToConsole(new Message("Failed to save the configuration.", true));
                        ex.printStackTrace();
                    }
                    return;
                }
                
                case "servers":
                {
                    BukkitTelnetClient.mainPanel.writeToConsole(new Message("-- Server List --", true));
                    for (Server server : Config.instance.getServers())
                    {
                        BukkitTelnetClient.mainPanel.writeToConsole(new Message(server.toString(), true));
                    }
                    BukkitTelnetClient.mainPanel.writeToConsole(new Message("-----------------", true));
                    return;
                }
                
                default:
                {
                    BukkitTelnetClient.mainPanel.writeToConsole(new Message("Unknown command. Type 'ftcp.help' for a list of commands.", true));
                    return;
                }
            }
        }
        
        try
        {
            if (verbose)
            {
                BukkitTelnetClient.mainPanel.writeToConsole(new Message("> " + text));
            }

            final OutputStream out = this.telnetClient.getOutputStream();
            if (out == null)
            {
                return;
            }

            this.telnetClient.getOutputStream().write((text + "\r\n").getBytes(StandardCharsets.UTF_8));
            this.telnetClient.getOutputStream().flush();
        }
        catch (IOException ex)
        {
            BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void sendDelayedCommand(final String text, final boolean verbose, final int delay)
    {
        final Timer timer = new Timer(delay, event -> sendCommand(text, verbose));
        timer.setRepeats(false);
        timer.start();
    }

    private void startConnectThread()
    {
        if (this.connectThread != null)
        {
            return;
        }

        this.connectThread = new Thread(() ->
        {
            final BTC_MainPanel btc = BukkitTelnetClient.mainPanel;

            try
            {
                BTC_ConnectionManager.this.telnetClient.connect(hostname, port);
                BTC_ConnectionManager.this.canDoDisconnect = true;

                btc.getBtnSend().setEnabled(true);
                btc.getTxtCommand().setEnabled(true);
                btc.getTxtCommand().requestFocusInWindow();

                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(telnetClient.getInputStream(), StandardCharsets.UTF_8)))
                {
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        String _loginName = null;
                        if (BTC_ConnectionManager.this.loginName == null)
                        {
                            _loginName = checkForLoginMessage(line);
                        }
                        if (_loginName != null)
                        {
                            BTC_ConnectionManager.this.loginName = _loginName;
                            updateTitle(true);
                            btc.getTxtNumPlayers().setText("Connected - Waiting for data");
                            
                            if (Config.instance.getPreferences().isAutoEnhanced())
                            {
                                sendDelayedCommand("telnet.enhanced", false, 100);
                            }
                            
                            if (Config.instance.getPreferences().isAutoEnhancedPlus())
                            {
                                sendDelayedCommand("telnet.enhancedplus", false, 100);
                            }
                        }
                        else
                        {
                            if (Enhancements.isDataset(line))
                            {
                                Enhancements.processDataset(line);
                            }
                            else
                            {
                                final Message message = new Message(line);
                                if (message.toString() != null)
                                {
                                    btc.writeToConsole(message);
                                }
                            }
                        }
                    }
                }

                triggerDisconnect();
            }
            catch (IOException ex)
            {
                btc.writeToConsole(new Message(ex.getMessage() + System.lineSeparator() + ExceptionUtils.getStackTrace(ex)));
            }

            finishDisconnect();

            BTC_ConnectionManager.this.connectThread = null;
        });
        this.connectThread.start();
    }

    public final boolean isConnected()
    {
        return this.telnetClient.isConnected();
    }
    
    public static String checkForLoginMessage(String message)
    {
        final Matcher matcher = LOGIN_MESSAGE.matcher(message);
        if (matcher.find())
        {
            return matcher.group(1);
        }

        return null;
    }

    public final void updateTitle(final boolean isConnected)
    {
        final BTC_MainPanel mainPanel = BukkitTelnetClient.mainPanel;
        if (mainPanel == null)
        {
            return;
        }

        String title;

        if (isConnected)
        {
            if (loginName == null)
            {
                title = String.format("FreedomTelnetClient+ - %s - %s:%d", BukkitTelnetClient.VERSION_STRING, hostname, port);
            }
            else
            {
                title = String.format("FreedomTelnetClient+ - %s - %s@%s:%d", BukkitTelnetClient.VERSION_STRING, loginName, hostname, port);
            }
        }
        else
        {
            title = String.format("FreedomTelnetClient+ - %s - Disconnected", BukkitTelnetClient.VERSION_STRING);
        }

        mainPanel.setTitle(title);
    }
}
