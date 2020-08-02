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
import java.sql.PreparedStatement;
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
    private final ConsoleCommandSender clogger = this.getServer().getConsoleSender();
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
    
    @Getter
    private PreparedStatement insertRegion;
    @Getter
    private PreparedStatement deleteRegion;
    @Getter
    private PreparedStatement deleteWarp;
    @Getter
    private PreparedStatement deleteNewsData;
    @Getter
    private PreparedStatement updateFinish;
    @Getter
    private PreparedStatement updateNews;
    @Getter
    private PreparedStatement updateProgress;
    @Getter
    private PreparedStatement updateStatus;
    @Getter
    private PreparedStatement updateInformations;
    @Getter
    private PreparedStatement updateWithoutUpdated;
    @Getter
    private PreparedStatement insertWarp;
    @Getter
    private PreparedStatement insertProject;
    @Getter
    private PreparedStatement insertNewsData;
    @Getter
    private PreparedStatement insertNewsBool;
    @Getter
    private PreparedStatement selectNewsData;
    @Getter
    private PreparedStatement selectNewsBool;
    @Getter
    private PreparedStatement selectWarps;
    @Getter
    private PreparedStatement selectRegions;
    @Getter
    private PreparedStatement selectProjects;
    @Getter
    private PreparedStatement selectPeopleData;
    @Getter
    private PreparedStatement selectNewsDataId;
    @Getter
    private PreparedStatement selectStatistic;
    @Getter
    private PreparedStatement selectStatisticPerDay;
    
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
                                    prepareStatements();
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
    private void prepareStatements() throws SQLException {
        
        String delete_region = "DELETE FROM mcmeproject_regions_data WHERE idregion = ? ;";
        String delete_warp = "DELETE FROM mcmeproject_warps_data WHERE idregion = ? ;";
        String delete_news_data = "DELETE FROM mcmeproject_news_data WHERE idproject = ? ;";
        
        String update_status_finish = "UPDATE mcmeproject_project_data SET status = ?, main = 0, endDate = ?, updated = ? WHERE idproject = ? ;";
        String update_news_bool = "UPDATE mcmeproject_news_bool SET bool = ? WHERE player_uuid = ? ;";
        String update_progress = "UPDATE mcmeproject_project_data SET percentage = ?, time = ?, updated = ? WHERE idproject = ? ;";
        String update_status = "UPDATE mcmeproject_project_data SET status = ?, endDate ?, updated = ? WHERE idproject = ? ;";
        String update_set = "UPDATE mcmeproject_project_data SET ? = ?, updated = ? WHERE idproject = ? ;";
        String update_no_updated = "UPDATE mcmeproject_project_data SET ? = ? WHERE idproject = ? ;";
        //Set easy

        String insert_warp = "INSERT INTO mcmeproject_warps_data (idproject, idregion, world, server, x, y, z ) VALUES (?,?,?,?,?,?,?) ;";
        String insert_project = "INSERT INTO mcmeproject_project_data (idproject, name, staff_uuid, startDate, percentage, link, time, description, updated, status, main, jobs, minutes, endDate, assistants, plcurrent) VALUES (?, ?, ?, ?, '0', 'nothing',?, ' ',?, ?, 0, ' ', '0', '0', ' ', ' ') ;";
        String insert_news_data = "INSERT INTO mcmeproject_news_data (idproject, player_uuid) VALUES (?,?) ;";
        String insert_news_bool = "INSERT INTO mcmeproject_news_bool (bool, player_uuid) VALUES(?,?);";
        String insert_regions = "INSERT INTO mcmeproject_regions_data (idproject, idregion, name, type, xlist, zlist, ymin, ymax, location, server, weight ) VALUES (?,?,?,?,?,?,?,?,?,?,?) ;";
        
        String select_news_data_id = "SELECT * FROM mcmeproject_news_data WHERE player_uuid = ? AND idproject = ? ;";
        String select_people_data = "SELECT * FROM mcmeproject_people_data WHERE idproject = ? ;";
        String select_news_bool = "SELECT * FROM mcmeproject_news_bool WHERE player_uuid = ? ;";
        String select_projects = "SELECT * FROM mcmeproject_project_data ;";
        String select_regions = "SELECT * FROM mcmeproject_regions_data ;";
        String select_warps = "SELECT * FROM mcmeproject_warps_data ;";
        String select_news_data = "SELECT * FROM mcmeproject_news_data WHERE player_uuid = ? ;";
        String select_statistic = "SELECT * FROM mcmeproject_statistics_data ;";
        String select_statistic_perday = "SELECT * FROM mcmeproject_statistics_data WHERE day = ? AND month = ? AND year = ? ;";
        
        deleteRegion = connection.prepareStatement(delete_region);
        deleteWarp = connection.prepareStatement(delete_warp);
        deleteNewsData = connection.prepareStatement(delete_news_data);
        
        updateFinish = connection.prepareStatement(update_status_finish);
        updateNews = connection.prepareStatement(update_news_bool);
        updateProgress = connection.prepareStatement(update_progress);
        updateStatus = connection.prepareStatement(update_status);
        updateInformations = connection.prepareStatement(update_set);
        updateWithoutUpdated = connection.prepareStatement(update_no_updated);
        
        insertWarp = connection.prepareStatement(insert_warp);
        insertProject = connection.prepareStatement(insert_project);
        insertNewsData = connection.prepareStatement(insert_news_data);
        insertNewsBool = connection.prepareStatement(insert_news_bool);
        insertRegion = connection.prepareStatement(insert_regions);
        
        selectNewsData = connection.prepareStatement(select_news_data);
        selectNewsBool = connection.prepareStatement(select_news_bool);
        selectWarps = connection.prepareStatement(select_warps);
        selectRegions = connection.prepareStatement(select_regions);
        selectProjects = connection.prepareStatement(select_projects);
        selectPeopleData = connection.prepareStatement(select_people_data);
        selectNewsDataId = connection.prepareStatement(select_news_data_id);
        selectStatistic = connection.prepareStatement(select_statistic);
        selectStatisticPerDay = connection.prepareStatement(select_statistic_perday);
        
        deleteRegion.setQueryTimeout(10);
        deleteWarp.setQueryTimeout(10);
        deleteNewsData.setQueryTimeout(10);
        updateWithoutUpdated.setQueryTimeout(10);
        updateFinish.setQueryTimeout(10);
        updateNews.setQueryTimeout(10);
        updateProgress.setQueryTimeout(10);
        updateStatus.setQueryTimeout(10);
        updateInformations.setQueryTimeout(10);
        insertWarp.setQueryTimeout(10);
        insertProject.setQueryTimeout(10);
        insertNewsData.setQueryTimeout(10);
        insertNewsBool.setQueryTimeout(10);
        insertRegion.setQueryTimeout(10);
        selectNewsData.setQueryTimeout(10);
        selectNewsBool.setQueryTimeout(10);
        selectWarps.setQueryTimeout(10);
        selectRegions.setQueryTimeout(10);
        selectProjects.setQueryTimeout(10);
        selectPeopleData.setQueryTimeout(10);
        selectNewsDataId.setQueryTimeout(10);
        selectStatistic.setQueryTimeout(10);
        selectStatisticPerDay.setQueryTimeout(10);
        
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
