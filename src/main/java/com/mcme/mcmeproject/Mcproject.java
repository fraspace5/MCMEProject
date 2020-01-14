/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject;

import com.mcme.mcmeproject.commands.ProjectCommandExecutor;
import com.mcme.mcmeproject.runnables.PlayersRunnable;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.listener.JobListener;
import com.mcme.mcmeproject.listener.PlayerListener;
import com.mcme.mcmeproject.runnables.SystemRunnable;
import com.mcme.mcmeproject.util.UpdaterCheck;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class Mcproject extends JavaPlugin implements Listener {

    static final Logger Logger = Bukkit.getLogger();
    @Getter
    public ConsoleCommandSender clogger = this.getServer().getConsoleSender();
    @Getter
    public Connection con;
    @Getter
    private File projectFolder;
    @Getter
    String host = this.getConfig().getString("host");
    @Getter
    String port = this.getConfig().getString("port");
    @Getter
    public String database = this.getConfig().getString("database");
    @Getter
    String username = this.getConfig().getString("username");
    @Getter
    String password = this.getConfig().getString("password");
    private File databool;

    private void checkUpdate() {
        final UpdaterCheck updater = new UpdaterCheck(this);
    }
    private File time;

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
            clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "MyBirthday" + ChatColor.DARK_GRAY + "] - " + ChatColor.RED + "Database error! (MyBirthday)");
            Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
            Bukkit.getPluginManager().disablePlugin(this);
        }

        try {
            onInitiateFile();
        } catch (IOException ex) {
            Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            PluginData.onLoad(projectFolder);
        } catch (IOException ex) {
            Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
        }

        getCommand("project").setExecutor(new ProjectCommandExecutor());
        getCommand("project").setTabCompleter(new ProjectCommandExecutor());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new JobListener(), this);
        clogger.sendMessage(ChatColor.GREEN + "---------------------------------------");
        clogger.sendMessage(ChatColor.BLUE + "MCMEProject Plugin v2.6 enabled!");
        clogger.sendMessage(ChatColor.GREEN + "---------------------------------------");
        try {
            PluginData.loadBoolean(databool);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
        }

        onStart();
        checkUpdate();
        if (PluginData.getMain() == true) {

            try {
                PluginData.loadTime(time);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @Override
    public void onDisable() {

        clogger.sendMessage(ChatColor.RED + "---------------------------------------");
        clogger.sendMessage(ChatColor.BLUE + "MCMEProject Plugin v2.6 disabled!");
        clogger.sendMessage(ChatColor.RED + "---------------------------------------");
        try {
            PluginData.onSave(projectFolder);
        } catch (IOException ex) {
            Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            PluginData.saveBoolean(databool);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onInitiateFile() throws IOException {
        projectFolder = new File(Bukkit.getServer().getPluginManager().getPlugin("McMeProject").getDataFolder(), "project");
        databool = new File(Bukkit.getServer().getPluginManager().getPlugin("McMeProject").getDataFolder(), "databool.yml");
        time = new File(Bukkit.getServer().getPluginManager().getPlugin("McMeProject").getDataFolder(), "otherdata.yml");

        if (!projectFolder.exists()) {

            projectFolder.mkdir();

        }

        if (!databool.exists()) {

            databool.createNewFile();
        }

        if (PluginData.getMain() == true) {

            if (!time.exists()) {

                time.createNewFile();
                PluginData.saveTime(time, System.currentTimeMillis());
            }

        }

    }

    public void openConnection() throws SQLException {
        if (con != null && !con.isClosed()) {
            return;
        }
        if (this.getPluginInstance().password.equalsIgnoreCase("default")) {
            clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "MyBirthday" + ChatColor.DARK_GRAY + "] - " + ChatColor.YELLOW + "Plugin INITIALIZED, change database information!");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {

            con = DriverManager.getConnection("jdbc:mysql://" + this.getPluginInstance().host + ":"
                    + this.getPluginInstance().port + "/"
                    + this.getPluginInstance().database + "?useSSL=false&allowPublicKeyRetrieval=true",
                    this.getPluginInstance().username,
                    this.getPluginInstance().password);
            clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "MyBirthday" + ChatColor.DARK_GRAY + "] - " + ChatColor.GREEN + "Database Found! ");

            new BukkitRunnable() {

                @Override
                public void run() {
                    try {
                        String st1 = "CREATE TABLE IF NOT EXISTS `mcmeproject_data`.`news_data` (\n"
                                + "  `player_uuid` VARCHAR(50) NOT NULL,\n"
                                + "  `idproject` VARCHAR(50) NOT NULL,\n"
                                + "  `bool` TINYINT NOT NULL,\n"
                                + "  PRIMARY KEY (`player_uuid`)); ";
                        String st2 = "CREATE TABLE IF NOT EXISTS `mcmeproject_data`.`news_bool` (\n"
                                + "  `player_uuid` VARCHAR(50) NOT NULL,\n"
                                + "  `bool` TINYINT NOT NULL,\n"
                                + "  PRIMARY KEY (`player_uuid`)); ";
                        String st3 = "CREATE TABLE IF NOT EXISTS `mcmeproject_data`.`project_data` (\n"
                                + "  `idproject` VARCHAR(50) NOT NULL,\n"
                                + "  `name` VARCHAR(80) NOT NULL,\n"
                                + "  `staff_uuid` VARCHAR(45) NOT NULL,\n"
                                + "  `startDate` DATE NOT NULL,\n"
                                + "  `endDate` DATE,\n"
                                + "  `status` VARCHAR(45) ,\n"
                                + "  `description` VARCHAR(200) ,\n"
                                + "  `main` VARCHAR(45) ,\n"
                                + "  `updated` MEDIUMTEXT NOT NULL,\n"
                                + "  `percentage` VARCHAR(45) ,\n"
                                + "  `link` VARCHAR(100) ,\n"
                                + "  `time` MEDIUMTEXT ,\n"
                                + "  `informed` LONGTEXT ,\n"
                                + "  PRIMARY KEY (`idproject`));";
                        String st4 = "CREATE TABLE IF NOT EXISTS `mcmeproject_data`.`staff_data` (\n"
                                + "  `staff_uuid` VARCHAR(50) NOT NULL,\n"
                                + "  `idproject` VARCHAR(45) NOT NULL,\n"
                                + "  PRIMARY KEY (`staff_uuid`));";
                        String st5 = "CREATE TABLE IF NOT EXISTS `mcmeproject_data`.`workinghours` (\n"
                                + "  `player_uuid` VARCHAR(50) NOT NULL,\n"
                                + "  `idproject` VARCHAR(45) NOT NULL,\n"
                                + "  `minutes` INT NOT NULL,\n"
                                + "  PRIMARY KEY (`player_uuid`));";
                        String st6 = "CREATE TABLE IF NOT EXISTS `mcmeproject_data`.`warps_data` (\n"
                                + "  `idregion` VARCHAR(50) NOT NULL,\n"
                                + "  `idproject` VARCHAR(45) NULL,\n"
                                + "  `world` VARCHAR(100) NOT NULL,\n"
                                + "  `x` FLOAT NOT NULL,\n"
                                + "  `y` FLOAT NOT NULL,\n"
                                + "  `z` FLOAT NOT NULL,\n"
                                + "  PRIMARY KEY (`idregion`));";
                        String st7 = "CREATE TABLE IF NOT EXISTS`mcmeproject_data`.`regions_data` (\n"
                                + "  `idproject` VARCHAR(45) NOT NULL,\n"
                                + "  `idregion` VARCHAR(45) NOT NULL,\n"
                                + "  `name` VARCHAR(45) NOT NULL,\n"
                                + "  `type` VARCHAR(45) NOT NULL,\n"
                                + "  `xlist` LONGTEXT NOT NULL,\n"
                                + "  `zlist` LONGTEXT NOT NULL,\n"
                                + "  `ymin` INT NOT NULL,\n"
                                + "  `ymax` INT NOT NULL,\n"
                                + "  `location` LONGTEXT NOT NULL,\n"
                                + "  PRIMARY KEY (`idproject`));";

                        con.createStatement().execute(st1 + st2 + st3 + st4 + st5 + st6 + st7);
                    } catch (SQLException ex) {
                        Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.runTaskAsynchronously(this.getPluginInstance());
        }

    }

    public void onStart() {

        PlayersRunnable.AddMinuteRunnable();
        PlayersRunnable.SetTodayUpdatedRunnable();
        SystemRunnable.startDatabaseRecoveryRunnable();
    }

    public Map<UUID, String> getProjects() {
        return PluginData.getProjectsAll();
    }

}
