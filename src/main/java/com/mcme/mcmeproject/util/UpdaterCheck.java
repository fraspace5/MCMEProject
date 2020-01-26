/*
 * Copyright (C) 2020 MCME (Fraspace5)
 *
 * This program is free software: you can redistribute it and/or modify
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
package com.mcme.mcmeproject.util;

import com.mcme.mcmeproject.Mcproject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Double.parseDouble;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author fraspace5
 */
public final class UpdaterCheck {

    private HttpURLConnection connection;
    private String WRITE_STRING;

    private String oldVersion = "0.0";
    private String newVersion = "0.0";

    public UpdaterCheck(JavaPlugin plugin) {

        oldVersion = plugin.getDescription().getVersion();

        new BukkitRunnable() {
            @Override
            public void run() {

                try {
                    //update link

                    connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/fraspace5/MCMEProject/master/src/main/resources/plugin.yml").openConnection();

                    connection.connect();

                    newVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine().substring(9);

                    if (parseDouble(newVersion) > parseDouble(oldVersion)) {

                        Mcproject.getPluginInstance().clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "MCMEProject" + ChatColor.DARK_GRAY + "] - " + "New version " + newVersion + " available for this Plugin");

                    } else {
                        Mcproject.getPluginInstance().clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "MCMEProject" + ChatColor.DARK_GRAY + "] - " + "No new version found!");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(UpdaterCheck.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }.runTaskAsynchronously(Mcproject.getPluginInstance());

    }

}
