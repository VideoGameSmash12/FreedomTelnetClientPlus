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

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import static javax.swing.JFileChooser.SAVE_DIALOG;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import me.videogamesm12.freedomtelnetclientplus.About;
import me.videogamesm12.freedomtelnetclientplus.data.*;
import me.videogamesm12.freedomtelnetclientplus.menu.JCommands;
import me.videogamesm12.freedomtelnetclientplus.config.Config;
import me.videogamesm12.freedomtelnetclientplus.menu.JFilters;
import me.videogamesm12.freedomtelnetclientplus.menu.JThemes;
import me.videogamesm12.freedomtelnetclientplus.models.PlayerTableModel;
import org.apache.commons.lang3.StringUtils;

public class BTC_MainPanel extends javax.swing.JFrame
{
    private final BTC_ConnectionManager connectionManager = new BTC_ConnectionManager();
    private final PlayerTableModel playerTableModel = new PlayerTableModel();
    //--
    private JButton btnDisconnect;
    private JButton btnSend;
    private JCheckBoxMenuItem chkAutoEnhanced;
    private JCheckBoxMenuItem chkAutoEnhancedPlus;
    private JCheckBoxMenuItem chkAutoScroll;
    private final JFilters filters = new JFilters();
    private final JThemes themes = new JThemes();
    private JTextPane mainOutput;
    private JScrollPane mainOutputScoll;
    private JTable tblPlayers;
    private JTextField txtCommand;
    private JLabel txtNumPlayers;
    private JComboBox<Server> txtServer;
    private JLabel txtTPS;
    private final JCheckBoxMenuItem chkClassicRanks = new JCheckBoxMenuItem("Use classic rank names");

    public BTC_MainPanel()
    {
        initComponents();
    }

    public void setup()
    {
        this.txtServer.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (e.getKeyChar() == KeyEvent.VK_ENTER)
                    BTC_MainPanel.this.saveServersAndTriggerConnect();
            }
        });

        this.loadServerList();

        final URL icon = this.getClass().getResource("/client-icon-dark.png");
        if (icon != null)
        {
            setIconImage(Toolkit.getDefaultToolkit().createImage(icon));
        }
        
        setupTablePopup();
        
        this.updateSettings();

        this.getConnectionManager().updateTitle(false);

        this.tblPlayers.setModel(playerTableModel);

        this.tblPlayers.getRowSorter().toggleSortOrder(0);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void sizeManagement()
    {
        switch (UIManager.getLookAndFeel().getName().toLowerCase())
        {
            case "darklaf":
            {
                BukkitTelnetClient.LOGGER.info("Resizing dimensions to look decent for the dark themes");
                btnSend.setPreferredSize(new Dimension(btnSend.getWidth(), 28));
                btnDisconnect.setPreferredSize(new Dimension(btnDisconnect.getWidth(), 28));
                txtCommand.setPreferredSize(new Dimension(txtCommand.getWidth(), 28));
                txtServer.setPreferredSize(new Dimension(txtServer.getWidth(), 28));
                break;
            }
            
            case "windows":
            {
                btnSend.setPreferredSize(new Dimension(btnSend.getWidth(), 23));
                btnDisconnect.setPreferredSize(new Dimension(btnDisconnect.getWidth(), 23));
                txtCommand.setPreferredSize(new Dimension(txtCommand.getWidth(), 23));
                txtServer.setPreferredSize(new Dimension(txtServer.getWidth(), 23));
                break;
            }
            
            default:
            {
                BukkitTelnetClient.LOGGER.info("lol debug time");
                BukkitTelnetClient.LOGGER.info(UIManager.getLookAndFeel().getName().toLowerCase());
                break;
            }
        }
    }

    public void writeToConsole(final Message message)
    {
        writeToConsoleImmediately(message);
    }

    private void updateSettings()
    {
        this.chkAutoScroll.setSelected(Config.instance.getPreferences().isAutoScroll());
        this.chkAutoEnhanced.setSelected(Config.instance.getPreferences().isAutoEnhanced());
        this.chkAutoEnhancedPlus.setSelected(Config.instance.getPreferences().isAutoEnhancedPlus());
    }

    private void writeToConsoleImmediately(Message msg)
    {
        final Message message = Filter.getFilteredMessage(msg);

        SwingUtilities.invokeLater(() ->
        {
            if (message.toString() != null)
            {
                final StyledDocument styledDocument = mainOutput.getStyledDocument();

                int startLength = styledDocument.getLength();

                try
                {
                    styledDocument.insertString(
                            styledDocument.getLength(),
                            message.getMessage() + System.lineSeparator(),
                            StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, message.getColor())
                    );
                }
                catch (BadLocationException ex)
                {
                    throw new RuntimeException(ex);
                }

                if (Config.instance.getPreferences().isAutoScroll() && BTC_MainPanel.this.mainOutput.getSelectedText() == null)
                {
                    final JScrollBar vScroll = mainOutputScoll.getVerticalScrollBar();

                    if (!vScroll.getValueIsAdjusting())
                    {
                        if (vScroll.getValue() + vScroll.getModel().getExtent() >= (vScroll.getMaximum() - 50))
                        {
                            BTC_MainPanel.this.mainOutput.setCaretPosition(startLength);

                            final Timer timer = new Timer(10, event -> vScroll.setValue(vScroll.getMaximum()));
                            timer.setRepeats(false);
                            timer.start();
                        }
                    }
                }
            }
        });
    }

    public final Player getSelectedPlayer()
    {
        return playerTableModel.getPlayer(tblPlayers.getSelectedRow());
    }

    public final void updatePlayerList(Enhancements.EnhancedModeDataset set)
    {
        EventQueue.invokeLater(() ->
        {
            playerTableModel.update(set.getPlayers());

            String text = playerTableModel.getRowCount() + " player" + (playerTableModel.getRowCount() != 1 ? "s" : "") + " online";
            txtNumPlayers.setText(text);
        });
    }
    
    public void updateTPSCounter(String tps)
    {
        if (tps == null)
        {
            txtTPS.setText(null);
        }
        else
        {
            txtTPS.setText(tps + " TPS");
        }
    }

    public static class PlayerPopupItem extends JMenuItem
    {
        private final Player player;

        public PlayerPopupItem(String text, Player player)
        {
            super(text);
            this.player = player;
        }

        public Player getPlayer()
        {
            return player;
        }
    }

    public static class PlayerPopupCommandItem extends PlayerPopupItem
    {
        private final PlayerCommand command;

        public PlayerPopupCommandItem(String text, Player player, PlayerCommand command)
        {
            super(text, player);
            this.command = command;
        }

        public PlayerCommand getCommand()
        {
            return command;
        }
    }

    public final void setupTablePopup()
    {
        this.tblPlayers.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(final MouseEvent mouseEvent)
            {
                final JTable table = BTC_MainPanel.this.tblPlayers;

                final int r = table.rowAtPoint(mouseEvent.getPoint());
                if (r >= 0 && r < table.getRowCount())
                {
                    table.setRowSelectionInterval(r, r);
                }
                else
                {
                    table.clearSelection();
                }

                final int rowindex = table.getSelectedRow();
                if (rowindex < 0)
                {
                    return;
                }

                if ((SwingUtilities.isRightMouseButton(mouseEvent) || mouseEvent.isControlDown()) && mouseEvent.getComponent() instanceof JTable)
                {
                    final Player player = getSelectedPlayer();
                    if (player != null)
                    {
                        final JPopupMenu popup = new JPopupMenu(player.getName());

                        final JMenuItem header = new JMenuItem("Apply action to " + player.getName() + ":");
                        header.setEnabled(false);
                        popup.add(header);

                        popup.addSeparator();

                        final ActionListener popupAction = actionEvent ->
                        {
                            Object _source = actionEvent.getSource();
                            if (_source instanceof PlayerPopupCommandItem)
                            {
                                final PlayerPopupCommandItem source = (PlayerPopupCommandItem) _source;
                                final String output = source.getCommand().getProcessed(source.getPlayer());
                                BTC_MainPanel.this.getConnectionManager().sendDelayedCommand(output, true, 100);
                            }
                            else if (_source instanceof PlayerPopupItem)
                            {
                                final PlayerPopupItem source = (PlayerPopupItem) _source;

                                final Player _player = source.getPlayer();

                                switch (actionEvent.getActionCommand())
                                {
                                    case "Copy IP":
                                    {
                                        copyToClipboard(_player.getIp());
                                        BTC_MainPanel.this.writeToConsole(new Message("Copied IP to clipboard: " + _player.getIp(), true));
                                        break;
                                    }
                                    case "Copy Name":
                                    {
                                        copyToClipboard(_player.getName());
                                        BTC_MainPanel.this.writeToConsole(new Message("Copied name to clipboard: " + _player.getName(), true));
                                        break;
                                    }
                                    case "Copy UUID":
                                    {
                                        copyToClipboard(_player.getUuid());
                                        BTC_MainPanel.this.writeToConsole(new Message("Copied UUID to clipboard: " + _player.getUuid(), true));
                                        break;
                                    }
                                }
                            }
                        };

                        for (PlayerCommand command : Config.instance.getPlayerCommands())
                        {
                            PlayerPopupCommandItem item = new PlayerPopupCommandItem(command.getName(), player, command);
                            item.addActionListener(popupAction);
                            popup.add(item);
                        }

                        popup.addSeparator();

                        JMenuItem item;

                        item = new PlayerPopupItem("Copy Name", player);
                        item.addActionListener(popupAction);
                        popup.add(item);

                        item = new PlayerPopupItem("Copy IP", player);
                        item.addActionListener(popupAction);
                        popup.add(item);

                        item = new PlayerPopupItem("Copy UUID", player);
                        item.addActionListener(popupAction);
                        popup.add(item);

                        popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                    }
                }
            }
        });
    }

    public void copyToClipboard(final String myString)
    {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(myString), null);
    }

    public final void loadServerList()
    {
        txtServer.removeAllItems();
        for (final Server serverEntry : Config.instance.getServers())
        {
            txtServer.addItem(serverEntry);

            if (serverEntry.isActive())
            {
                txtServer.setSelectedItem(serverEntry);
            }
        }
    }

    public final void saveServersAndTriggerConnect()
    {
        final Object selectedItem = txtServer.getSelectedItem();
        if (selectedItem == null)
        {
            return;
        }

        Server entry;
        if (selectedItem instanceof Server)
        {
            entry = (Server) selectedItem;
        }
        else
        {
            final String serverAddress = StringUtils.trimToNull(selectedItem.toString());
            if (serverAddress == null)
            {
                return;
            }

            String serverName = JOptionPane.showInputDialog(this, "Enter server name:", "Server Name", JOptionPane.PLAIN_MESSAGE);
            if (serverName == null)
            {
                return;
            }

            serverName = StringUtils.trimToEmpty(serverName);
            if (serverName.isEmpty())
            {
                serverName = "Unnamed";
            }

            entry = Server.builder().name(serverName).ip(serverAddress).active(true).build(); //(serverName, serverAddress);

            //BukkitTelnetClient.config2.getServers().add(entry);
            Config.instance.getServers().add(entry);
        }

        for (Server server : Config.instance.getServers())
        {
            if (server.equals(entry))
            {
                entry = server;
            }
            else
            {
                server.setActive(false);
            }
        }

        Config.save();

        loadServerList();

        getConnectionManager().triggerConnect(entry.getIp());
    }

    private void initComponents()
    {
        JSplitPane splitPane = new JSplitPane();
        JPanel mainPanel = new JPanel();
        JLabel commandLabel = new JLabel();
        JLabel serverLabel = new JLabel();
        JPanel playerListPanel = new JPanel();
        JScrollPane tblPlayersScroll = new JScrollPane();
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu();
        JMenuItem saveOutputOption = new JMenuItem();
        JMenuItem clearOutputOption = new JMenuItem();
        JMenuItem quitMenuOption = new JMenuItem();
        JCommands commandsMenu = new JCommands();
        JMenu settingsMenu = new JMenu();
        JMenu automationMenu = new JMenu();
        JMenu helpMenu = new JMenu();
        JMenuItem aboutOption = new JMenuItem();
        //--
        mainOutputScoll = new javax.swing.JScrollPane();
        mainOutput = new javax.swing.JTextPane();
        btnDisconnect = new javax.swing.JButton();
        btnSend = new javax.swing.JButton();
        txtServer = new javax.swing.JComboBox<>();
        txtCommand = new javax.swing.JTextField();
        tblPlayers = new javax.swing.JTable();
        txtNumPlayers = new javax.swing.JLabel();
        txtTPS = new javax.swing.JLabel();
        chkAutoScroll = new javax.swing.JCheckBoxMenuItem();
        chkAutoEnhanced = new javax.swing.JCheckBoxMenuItem();
        chkAutoEnhancedPlus = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FreedomTelnetClient+");
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                Config.save();
            }
        });

        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(1.0);

        mainOutput.setEditable(false);
        mainOutput.setFont(new java.awt.Font("Courier New", Font.PLAIN, 12)); // NOI18N
        mainOutputScoll.setViewportView(mainOutput);

        btnDisconnect.setText("Connect");
        btnDisconnect.addActionListener(this::btnDisconnectActionPerformed);

        btnSend.setText("Send");
        btnSend.setEnabled(false);
        btnSend.addActionListener(this::btnSendActionPerformed);

        txtServer.setEditable(true);
        txtServer.setMinimumSize(new java.awt.Dimension(125, 21));
        txtServer.setPreferredSize(new java.awt.Dimension(125, 21));

        txtCommand.setEnabled(false);
        txtCommand.setMinimumSize(new java.awt.Dimension(7, 21));
        txtCommand.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                txtCommandKeyPressed(evt);
            }
        });

        commandLabel.setText("Command:");

        serverLabel.setText("Server:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainOutputScoll)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(serverLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(commandLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCommand, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtServer, 0, 518, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnDisconnect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainOutputScoll, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(commandLabel)
                        .addComponent(btnSend)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverLabel)
                    .addComponent(btnDisconnect)
                    .addComponent(txtServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        splitPane.setLeftComponent(mainPanel);

        tblPlayers.setAutoCreateRowSorter(true);
        tblPlayers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblPlayersScroll.setViewportView(tblPlayers);
        tblPlayers.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblPlayers.getColumnModel().getColumnCount() > 0)
        {
            tblPlayers.getColumnModel().getColumn(0).setHeaderValue("Username");
            tblPlayers.getColumnModel().getColumn(1).setHeaderValue("Date Changed");
        }

        txtNumPlayers.setText("Not connected");

        txtTPS.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(playerListPanel);
        playerListPanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblPlayersScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNumPlayers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTPS)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tblPlayersScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNumPlayers)
                    .addComponent(txtTPS))
                .addGap(20, 20, 20))
        );

        splitPane.setRightComponent(playerListPanel);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText("File");

        saveOutputOption.setText("Save Output");
        saveOutputOption.addActionListener(this::saveOutputOptionActionPerformed);
        fileMenu.add(saveOutputOption);

        clearOutputOption.setText("Clear Output");
        clearOutputOption.addActionListener(this::clearOutputOptionActionPerformed);
        fileMenu.add(clearOutputOption);
        fileMenu.addSeparator();

        quitMenuOption.setText("Quit");
        quitMenuOption.addActionListener(this::quitMenuOptionActionPerformed);
        fileMenu.add(quitMenuOption);

        menuBar.add(fileMenu);

        commandsMenu.setText("Commands");
        menuBar.add(commandsMenu);

        settingsMenu.setText("Settings");
        settingsMenu.add(themes);
        settingsMenu.add(filters);

        automationMenu.setText("Automation");
        chkAutoScroll.setSelected(true);
        chkAutoScroll.setText("AutoScroll");
        chkAutoScroll.addActionListener(this::chkAutoScrollActionPerformed);
        automationMenu.add(chkAutoScroll);

        chkAutoEnhanced.setSelected(true);
        chkAutoEnhanced.setText("AutoEnhanced");
        chkAutoEnhanced.addActionListener(this::chkAutoEnhancedActionPerformed);
        automationMenu.add(chkAutoEnhanced);

        chkAutoEnhancedPlus.setSelected(true);
        chkAutoEnhancedPlus.setText("AutoEnhanced+");
        chkAutoEnhancedPlus.setToolTipText("The ZeroTelnetClient introduces a feature that allows you to see the server's TPS. This automatically enables that behavior.");
        chkAutoEnhancedPlus.addActionListener(this::chkAutoEnhancedPlusActionPerformed);
        automationMenu.add(chkAutoEnhancedPlus);

        settingsMenu.add(automationMenu);
        settingsMenu.addSeparator();

        chkClassicRanks.addActionListener(e -> Config.instance.getPreferences().setUsingLegacyRanks(chkClassicRanks.getState()));
        settingsMenu.add(chkClassicRanks);

        menuBar.add(settingsMenu);

        helpMenu.setText("Help");

        aboutOption.setText("About");
        aboutOption.addActionListener(this::aboutOptionActionPerformed);
        helpMenu.add(aboutOption);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
        menuBar.getAccessibleContext().setAccessibleName("menuBar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1027, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(splitPane))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void quitMenuOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuOptionActionPerformed
        BukkitTelnetClient.quit();
    }//GEN-LAST:event_quitMenuOptionActionPerformed

    private void clearOutputOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearOutputOptionActionPerformed
        this.mainOutput.setText(null);
    }//GEN-LAST:event_clearOutputOptionActionPerformed

    private void chkAutoScrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAutoScrollActionPerformed
        //BukkitTelnetClient.LOGGER.log(Level.INFO, "Setting autoscroll to {0}", chkAutoScroll.isSelected());
        Config.instance.getPreferences().setAutoScroll(chkAutoScroll.isSelected());
    }//GEN-LAST:event_chkAutoScrollActionPerformed

    private void chkAutoEnhancedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAutoEnhancedActionPerformed
        Config.instance.getPreferences().setAutoEnhanced(chkAutoEnhanced.isSelected());
    }//GEN-LAST:event_chkAutoEnhancedActionPerformed

    private void aboutOptionActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_aboutOptionActionPerformed
    {//GEN-HEADEREND:event_aboutOptionActionPerformed
        About about = new About();
        
        // Thanks, StackOverflow! https://stackoverflow.com/questions/144892/how-to-center-a-window-in-java
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - about.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - about.getHeight()) / 2);
        about.setLocation(x, y);
        
        about.setVisible(true);
    }//GEN-LAST:event_aboutOptionActionPerformed

    private void txtCommandKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_txtCommandKeyPressed
    {//GEN-HEADEREND:event_txtCommandKeyPressed
        if (!txtCommand.isEnabled())
        {
            return;
        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            getConnectionManager().sendCommand(txtCommand.getText());
            txtCommand.selectAll();
        }
    }//GEN-LAST:event_txtCommandKeyPressed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt)
    {
        if (!btnSend.isEnabled())
        {
            return;
        }
        getConnectionManager().sendCommand(txtCommand.getText());
        txtCommand.selectAll();
    }

    private void btnDisconnectActionPerformed(java.awt.event.ActionEvent evt)
    {
        if (getConnectionManager().isConnected())
            getConnectionManager().triggerDisconnect();
        else
            saveServersAndTriggerConnect();
    }
    
    private void chkAutoEnhancedPlusActionPerformed(java.awt.event.ActionEvent evt)
    {
        Config.instance.getPreferences().setAutoEnhancedPlus(chkAutoEnhancedPlus.isSelected());
    }

    private void saveOutputOptionActionPerformed(java.awt.event.ActionEvent evt)
    {
        JFileChooser saveLogsChooser = new JFileChooser();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        //--
        saveLogsChooser.setDialogTitle("Specify where to save the output to");
        saveLogsChooser.setDialogType(SAVE_DIALOG);
        //--
        FileNameExtensionFilter logFilter = new FileNameExtensionFilter("Log files (*.log)", "log");
        saveLogsChooser.addChoosableFileFilter(logFilter);
        saveLogsChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text Documents (*.txt)", "txt"));
        //--
        saveLogsChooser.setSelectedFile(new File(dateFormat.format(new Date()) + ".log"));
        saveLogsChooser.setFileFilter(logFilter);
        //--
        int result = saveLogsChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            writeToConsoleImmediately(new Message("Saving output to " + saveLogsChooser.getSelectedFile().getName() + "...", true));
            try 
            {
                BufferedWriter out = new BufferedWriter(new FileWriter(saveLogsChooser.getSelectedFile()));
                //--
                out.write(String.format("--- FreedomTelnetClient+ Output - %s ---\n", new Date()));
                out.write(mainOutput.getText().replaceAll("\r", ""));
                out.close();
                //--
                writeToConsoleImmediately(new Message("Saved output to " + saveLogsChooser.getSelectedFile().getAbsolutePath() + ".", true));
            }
            catch (Exception e)
            {
                writeToConsoleImmediately(new Message("Failed to save the output to " + saveLogsChooser.getSelectedFile().getAbsolutePath() + ".", true));
                e.printStackTrace();
            }
        }
    }

    public javax.swing.JButton getBtnConnect()
    {
        return btnDisconnect;
    }

    public javax.swing.JButton getBtnSend()
    {
        return btnSend;
    }

    public javax.swing.JTextField getTxtCommand()
    {
        return txtCommand;
    }

    public javax.swing.JComboBox<Server> getTxtServer()
    {
        return txtServer;
    }
    
    public JLabel getTxtNumPlayers()
    {
        return txtNumPlayers;
    }

    public BTC_ConnectionManager getConnectionManager()
    {
        return connectionManager;
    }
}
