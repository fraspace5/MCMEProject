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

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcme.mcmeproject.commands.ProjectCommandExecutor;
import com.mcme.mcmeproject.runnables.PlayersRunnable;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.listener.JobListener;
import com.mcme.mcmeproject.listener.PlayerListener;
import com.mcme.mcmeproject.runnables.SystemRunnable;
import com.mcme.mcmeproject.util.UpdaterCheck;
import com.mcmiddleearth.thegaffer.ext.ExternalProjectHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class Mcproject extends JavaPlugin implements Listener, PluginMessageListener, ExternalProjectHandler {

    static final Logger Logger = Bukkit.getLogger();
    @Getter
    public ConsoleCommandSender clogger = this.getServer().getConsoleSender();
    @Getter
    public Connection con;

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

    @Getter
    public String nameserver;

    private void checkUpdate() {
        final UpdaterCheck updater = new UpdaterCheck(this);
    }

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
            Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getCommand("project").setExecutor(new ProjectCommandExecutor());
        getCommand("project").setTabCompleter(new ProjectCommandExecutor());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "mcme:project");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "mcme:project", this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new JobListener(), this);
        clogger.sendMessage(ChatColor.GREEN + "---------------------------------------");
        clogger.sendMessage(ChatColor.BLUE + "MCMEProject Plugin v" + this.getDescription().getVersion() + " enabled!");
        clogger.sendMessage(ChatColor.GREEN + "---------------------------------------");
        if (this.isEnabled()) {
            nameserver = "default";
            onStart();
            checkUpdate();
            ConnectionRunnable();

        }

    }

    @Override
    public void onDisable() {

        clogger.sendMessage(ChatColor.RED + "---------------------------------------");
        clogger.sendMessage(ChatColor.BLUE + "MCMEProject Plugin v" + this.getDescription().getVersion() + " disabled!");
        clogger.sendMessage(ChatColor.RED + "---------------------------------------");

    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("mcme:project")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equals("reload")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                String somedata = msgin.readUTF(); // Read the data in the same way you wrote it

                switch (somedata) {

                    case "all":
                        PluginData.loadProjects();
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                PluginData.loadRegions();
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        PluginData.loadWarps();
                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {
                                                PluginData.loadAllDynmap();

                                            }

                                        }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                                    }

                                }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 20L);

                        break;
                    case "map":
                        PluginData.loadAllDynmap();
                        break;
                    case "regions":
                        PluginData.loadRegions();
                        break;
                    case "warps":
                        PluginData.loadWarps();
                        break;
                    case "projects":
                        PluginData.loadProjects();
                        break;
                    default:
                        PluginData.loadProjects();
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                PluginData.loadRegions();
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        PluginData.loadWarps();
                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {
                                                PluginData.loadAllDynmap();

                                            }

                                        }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                                    }

                                }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                        break;

                }
            } catch (IOException ex) {
                Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (subchannel.equals("GetServer")) {
            String servern = in.readUTF();
            nameserver = servern;
        }

    }

    public void sendNameServer(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("GetServer");

        player.sendPluginMessage(this, "mcme:project", out.toByteArray());
    }

    public void sendReload(Player player, String s) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Forward"); // So BungeeCord knows to forward it
        out.writeUTF("ALL");
        out.writeUTF("reload"); // The channel name to check if this your data

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        switch (s) {

            case "all":
                try {
                msgout.writeUTF("all"); // You can do anything you want with msgout

            } catch (IOException exception) {
                exception.printStackTrace();
            }
            break;
            case "map":
                try {
                msgout.writeUTF("map");

            } catch (IOException exception) {
                exception.printStackTrace();
            }
            break;
            case "regions":
                try {
                msgout.writeUTF("regions");

            } catch (IOException exception) {
                exception.printStackTrace();
            }
            break;
            case "warps":
                try {
                msgout.writeUTF("warps");

            } catch (IOException exception) {
                exception.printStackTrace();
            }
            break;
            case "projects":
                try {
                msgout.writeUTF("projects"); // You can do anything you want with msgout

            } catch (IOException exception) {
                exception.printStackTrace();
            }
            break;
            default:
                 try {
                msgout.writeUTF("all"); // You can do anything you want with msgout

            } catch (IOException exception) {
                exception.printStackTrace();
            }
            break;

        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        player.sendPluginMessage(this, "mcme:project", out.toByteArray());
    }


    /*
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
     */
    public void openConnection() throws SQLException {
        if (con != null && !con.isClosed()) {
            return;
        }
        if (Mcproject.getPluginInstance().password.equalsIgnoreCase("default")) {
            clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "MCMEProject" + ChatColor.DARK_GRAY + "] - " + ChatColor.YELLOW + "Plugin INITIALIZED, change database information!");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {

            con = DriverManager.getConnection("jdbc:mysql://" + Mcproject.getPluginInstance().host + ":"
                    + Mcproject.getPluginInstance().port + "/"
                    + Mcproject.getPluginInstance().database + "?useSSL=false&allowPublicKeyRetrieval=true",
                    Mcproject.getPluginInstance().username,
                    Mcproject.getPluginInstance().password);
            clogger.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "MCMEProject" + ChatColor.DARK_GRAY + "] - " + ChatColor.GREEN + "Database Found! ");

            new BukkitRunnable() {

                @Override
                public void run() {
                    try {
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

                        con.createStatement().execute(st1);
                        con.createStatement().execute(st2);

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    con.createStatement().execute(st3);
                                    con.createStatement().execute(st5);
                                } catch (SQLException ex) {
                                    Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                         new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    con.createStatement().execute(st6);
                                    con.createStatement().execute(st7);
                                } catch (SQLException ex) {
                                    Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 40L);

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    con.createStatement().execute(st8);
                                } catch (SQLException ex) {
                                    Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 60L);

                    } catch (SQLException ex) {
                        Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.runTaskAsynchronously(Mcproject.getPluginInstance());
        }

    }

    public Set<String> getProjectNames() {
        return PluginData.getProjectsAll().keySet();
    }

    public void ConnectionRunnable() {

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    if (!con.isValid(2)) {

                        openConnection();

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskTimer(Mcproject.getPluginInstance(), 60L, 100L);

    }

    public void onStart() {
        SystemRunnable.startDatabaseRecoveryRunnable();
        PlayersRunnable.AddMinuteRunnable();
        PlayersRunnable.SetTodayUpdatedRunnable();
        SystemRunnable.PlayersDataBlocksRunnable();
        SystemRunnable.variableDataMinutesRunnable();
        SystemRunnable.variableDataBlocksRunnable();
        SystemRunnable.statisticAllRunnable();

    }

    public Map<String, ProjectData> getProjects() {
        return PluginData.projectsAll;
    }

}
