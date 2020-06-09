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
package com.mcme.mcmeproject;

import com.mcme.mcmeproject.commands.ProjectCommandExecutor;
import com.mcme.mcmeproject.runnables.PlayersRunnable;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.listener.JobListener;
import com.mcme.mcmeproject.listener.PlayerListener;
import com.mcme.mcmeproject.runnables.SystemRunnable;
import com.mcme.mcmeproject.util.bungee;
import com.mcmiddleearth.thegaffer.ext.ExternalProjectHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class Mcproject extends JavaPlugin implements Listener, ExternalProjectHandler {

    static final Logger Logger = Bukkit.getLogger();
    @Getter
    private ConsoleCommandSender clogger = this.getServer().getConsoleSender();
    @Getter
    private Connection connection;

    /**
     * Database variables
     */
    private final String host = this.getConfig().getString("host");
    private final String port = this.getConfig().getString("port");
    private final String database = this.getConfig().getString("database");
    private final String username = this.getConfig().getString("username");
    private final String password = this.getConfig().getString("password");
    /**
     *
     */

    @Setter
    @Getter
    private String nameserver;

    @Getter
    private static Mcproject pluginInstance;

    @Override
    public void onEnable() {
        pluginInstance = this;
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults();
        try {
            openConnection();
        } catch (SQLException ex) {

            clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "MCMEProject" + ChatColor.DARK_GRAY + "] - " + ChatColor.RED + "Database error! (McMeProject)");
            Bukkit.getPluginManager().disablePlugin(this);

        }

        getCommand("project").setExecutor(new ProjectCommandExecutor());
        getCommand("project").setTabCompleter(new ProjectCommandExecutor());

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new bungee());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new JobListener(), this);

        clogger.sendMessage(ChatColor.GREEN + "---------------------------------------");
        clogger.sendMessage(ChatColor.BLUE + "MCMEProject Plugin v" + this.getDescription().getVersion() + " enabled!");
        clogger.sendMessage(ChatColor.GREEN + "---------------------------------------");

        if (this.isEnabled()) {
            Mcproject.getPluginInstance().setNameserver("default");
            onStart();
            SystemRunnable.ConnectionRunnable();

        }

    }

    @Override
    public void onDisable() {

        clogger.sendMessage(ChatColor.RED + "---------------------------------------");
        clogger.sendMessage(ChatColor.BLUE + "MCMEProject Plugin v" + this.getDescription().getVersion() + " disabled!");
        clogger.sendMessage(ChatColor.RED + "---------------------------------------");
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        if (Mcproject.getPluginInstance().password.equalsIgnoreCase("default")) {
            clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "MCMEProject" + ChatColor.DARK_GRAY + "] - " + ChatColor.YELLOW + "Plugin INITIALIZED, change database information!");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            new BukkitRunnable() {

                @Override
                public void run() {
                    try {
                        connection = DriverManager.getConnection("jdbc:mysql://" + Mcproject.getPluginInstance().host + ":"
                                + Mcproject.getPluginInstance().port + "/"
                                + Mcproject.getPluginInstance().database + "?useSSL=false&allowPublicKeyRetrieval=true",
                                Mcproject.getPluginInstance().username,
                                Mcproject.getPluginInstance().password);
                        clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "MCMEProject" + ChatColor.DARK_GRAY + "] - " + ChatColor.GREEN + "Database Found! ");

                        String st1 = "CREATE TABLE IF NOT EXISTS `" + Mcproject.getPluginInstance().database + "`.`mcmeproject_news_data` (\n"
                                + "  `player_uuid` VARCHAR(50) NOT NULL,\n"
                                + "  `idproject` VARCHAR(50) NOT NULL);";
                        String st2 = "CREATE TABLE IF NOT EXISTS `" + Mcproject.getPluginInstance().database + "`.`mcmeproject_news_bool` (\n"
                                + "  `player_uuid` VARCHAR(50) NOT NULL,\n"
                                + "  `bool` BOOLEAN NOT NULL);";
                        final String st3 = "CREATE TABLE IF NOT EXISTS `" + Mcproject.getPluginInstance().database + "`.`mcmeproject_project_data` (\n"
                                + "  `idproject` VARCHAR(50) NOT NULL,\n"
                                + "  `name` VARCHAR(80) NOT NULL,\n"
                                + "  `staff_uuid` VARCHAR(45) NOT NULL,\n"
                                + "  `startDate` MEDIUMTEXT,\n"
                                + "  `endDate` MEDIUMTEXT,\n"
                                + "  `status` VARCHAR(45) ,\n"
                                + "  `description` VARCHAR(200) ,\n"
                                + "  `main` BOOLEAN ,\n"
                                + "  `updated` MEDIUMTEXT NOT NULL,\n"
                                + "  `percentage` VARCHAR(45) ,\n"
                                + "  `link` VARCHAR(100) ,\n"
                                + "  `time` MEDIUMTEXT ,\n"
                                + "  `jobs` LONGTEXT ,\n"
                                + "  `assistants` LONGTEXT ,\n"
                                + "  `minutes` INT ,\n"
                                + "  `blocks` INT ,\n"
                                + "  `plcurrent` LONGTEXT ,\n"
                                + "  PRIMARY KEY (`idproject`));";
                        final String st5 = "CREATE TABLE IF NOT EXISTS `" + Mcproject.getPluginInstance().database + "`.`mcmeproject_people_data` (\n"
                                + "  `player_uuid` VARCHAR(50) NOT NULL,\n"
                                + "  `idproject` VARCHAR(45) NOT NULL,\n"
                                + "  `blocks` MEDIUMTEXT,\n"
                                + "  `lastplayed` MEDIUMTEXT);";
                        final String st6 = "CREATE TABLE IF NOT EXISTS `" + Mcproject.getPluginInstance().database + "`.`mcmeproject_warps_data` (\n"
                                + "  `idregion` VARCHAR(50) NOT NULL,\n"
                                + "  `idproject` VARCHAR(45) NULL,\n"
                                + "  `world` VARCHAR(100) NOT NULL,\n"
                                + "  `server` VARCHAR(100) NOT NULL,\n"
                                + "  `x` FLOAT NOT NULL,\n"
                                + "  `y` FLOAT NOT NULL,\n"
                                + "  `z` FLOAT NOT NULL,\n"
                                + "  PRIMARY KEY (`idregion`));";
                        final String st7 = "CREATE TABLE IF NOT EXISTS `" + Mcproject.getPluginInstance().database + "`.`mcmeproject_regions_data` (\n"
                                + "  `idproject` VARCHAR(45) NOT NULL,\n"
                                + "  `idregion` VARCHAR(45) NOT NULL,\n"
                                + "  `name` VARCHAR(45) NOT NULL,\n"
                                + "  `type` VARCHAR(45) NOT NULL,\n"
                                + "  `xlist` LONGTEXT NOT NULL,\n"
                                + "  `zlist` LONGTEXT NOT NULL,\n"
                                + "  `ymin` INT NOT NULL,\n"
                                + "  `ymax` INT NOT NULL,\n"
                                + "  `weight` INT NOT NULL,\n"
                                + "  `location` LONGTEXT NOT NULL,\n"
                                + "  `server` VARCHAR(100) NOT NULL,\n"
                                + "  PRIMARY KEY (`idregion`));";
                        String st8 = "CREATE TABLE IF NOT EXISTS `" + Mcproject.getPluginInstance().database + "`.`mcmeproject_statistics_data` (\n"
                                + "  `day` VARCHAR(25) ,\n"
                                + "  `month` VARCHAR(25) ,\n"
                                + "  `year` VARCHAR(25) ,\n"
                                + "  `blocks` INT ,\n"
                                + "  `minutes` INT ,\n"
                                + "  `projects` LONGTEXT ,\n"
                                + "  `players` LONGTEXT );";

                        connection.createStatement().execute(st1);
                        connection.createStatement().execute(st2);
                        connection.createStatement().execute(st3);

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {

                                    connection.createStatement().execute(st5);
                                    connection.createStatement().execute(st6);
                                    connection.createStatement().execute(st7);
                                } catch (SQLException ex) {
                                    Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 40L);
                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    connection.createStatement().execute(st8);
                                } catch (SQLException ex) {
                                    Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 40L);

                    } catch (SQLException ex) {
                        Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.runTaskAsynchronously(Mcproject.getPluginInstance());
        }

    }

    private void onStart() {
        SystemRunnable.startDatabaseRecoveryRunnable();
        PlayersRunnable.AddMinuteRunnable();
        PlayersRunnable.SetTodayUpdatedRunnable();
        SystemRunnable.PlayersDataBlocksRunnable();
        SystemRunnable.variableDataMinutesRunnable();
        SystemRunnable.variableDataBlocksRunnable();
        SystemRunnable.statisticAllRunnable();

    }

    
    /**
     * Prepare Statements for SQL database
     */
    private void prepareStatements() {

    }

    /**
     * API method 1/2
     *
     * @return All projects
     */
    public Map<String, ProjectData> getProjects() {
        return PluginData.getProjectsAll();
    }

    /**
     * API Method 2/2
     *
     * @return All projects names
     */
    @Override
    public Set<String> getProjectNames() {
        return PluginData.getProjectsAll().keySet();
    }

}
