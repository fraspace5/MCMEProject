/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author fraspace5
 */
public class UpdaterCheck {

    private HttpURLConnection connection;
    private String WRITE_STRING;

    private String oldVersion = "0.0";
    private String newVersion = "0.0";

    public UpdaterCheck(JavaPlugin plugin) {

        oldVersion = plugin.getDescription().getVersion();
        System.out.println(oldVersion);
        try {
            connection = (HttpURLConnection) new URL("https://github.com/fraspace5/MCMEProject/blob/master/src/main/resources/plugin.yml").openConnection();
            connection.connect();

            newVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            System.out.println(newVersion);
        } catch (IOException e) {
            return;
        }

    }
}
