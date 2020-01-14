/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
                    
                    connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/fraspace5/MyBirthday/1.13.2/src/main/resources/plugin.yml").openConnection();

                    connection.connect();

                    newVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine().substring(9);

                    if (parseDouble(newVersion) > parseDouble(oldVersion)) {

                        Mcproject.getPluginInstance().clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "MyBirthday" + ChatColor.DARK_GRAY + "] - " + "New version " + newVersion + " available for this Plugin");

                    } else {
                        Mcproject.getPluginInstance().clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "MyBirthday" + ChatColor.DARK_GRAY + "] - " + "No new version found!");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(UpdaterCheck.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }.runTaskAsynchronously(Mcproject.getPluginInstance());

    }

}
