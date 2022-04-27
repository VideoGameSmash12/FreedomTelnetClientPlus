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
package me.videogamesm12.freedomtelnetclientplus;

import java.awt.*;
import java.net.URL;
import me.StevenLawson.BukkitTelnetClient.BukkitTelnetClient;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class About extends JFrame
{
    public About()
    {
        initComponents();
        // --
        final URL icon = this.getClass().getResource("/client-icon-dark.png");
        if (icon != null)
        {
            setIconImage(Toolkit.getDefaultToolkit().createImage(icon));
        }
        // --
        clientNameLabel.setText("FreedomTelnetClient+");
        clientVersionLabel.setText(String.format("Version %s, compiled on %s", BukkitTelnetClient.getVersionString(), BukkitTelnetClient.getBuildDate()));
        clientAuthorLabel.setText(String.format("By %s", StringUtils.join(BukkitTelnetClient.DEVELOPERS, ", ")));
    }

    private void initComponents()
    {
        jPanel = new javax.swing.JPanel();
        inputSeparator = new javax.swing.JSeparator();
        OKButton = new javax.swing.JButton();
        iconLabel = new javax.swing.JLabel();
        clientInfoPanel = new javax.swing.JPanel();
        clientNameLabel = new javax.swing.JLabel();
        clientVersionLabel = new javax.swing.JLabel();
        clientAuthorLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About FreedomTelnetClient+");
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        OKButton.setText("OK");
        OKButton.addActionListener(this::OKButtonActionPerformed);

        iconLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client-icon-small.png"))); // NOI18N
        iconLabel.setText("LOGO");

        clientNameLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        clientNameLabel.setText("{CLIENT NAME}");

        clientVersionLabel.setText("Version {VERSION}, compiled on {DATE}");

        clientAuthorLabel.setText("By {AUTHOR}");

        javax.swing.GroupLayout clientInfoPanelLayout = new javax.swing.GroupLayout(clientInfoPanel);
        clientInfoPanel.setLayout(clientInfoPanelLayout);
        clientInfoPanelLayout.setHorizontalGroup(
            clientInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientInfoPanelLayout.createSequentialGroup()
                .addGroup(clientInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(clientNameLabel)
                    .addComponent(clientVersionLabel)
                    .addComponent(clientAuthorLabel))
                .addGap(0, 122, Short.MAX_VALUE))
        );
        clientInfoPanelLayout.setVerticalGroup(
            clientInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientInfoPanelLayout.createSequentialGroup()
                .addComponent(clientNameLabel)
                .addGap(0, 0, 0)
                .addComponent(clientVersionLabel)
                .addGap(0, 0, 0)
                .addComponent(clientAuthorLabel))
        );

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(OKButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(inputSeparator)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addComponent(iconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clientInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(iconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clientInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 175, Short.MAX_VALUE)
                .addComponent(inputSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(OKButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        this.dispose();
    }

    private javax.swing.JButton OKButton;
    private javax.swing.JLabel clientAuthorLabel;
    private javax.swing.JPanel clientInfoPanel;
    private javax.swing.JLabel clientNameLabel;
    private javax.swing.JLabel clientVersionLabel;
    private javax.swing.JLabel iconLabel;
    private javax.swing.JSeparator inputSeparator;
    private javax.swing.JPanel jPanel;
}
