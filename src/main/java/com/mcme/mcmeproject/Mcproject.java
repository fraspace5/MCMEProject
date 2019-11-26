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
import com.mcme.mcmeproject.util.UpdaterCheck;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
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

/**
 *
 * @author Fraspace5
 */
public class Mcproject extends JavaPlugin implements Listener {

    static final Logger Logger = Bukkit.getLogger();
    @Getter
    ConsoleCommandSender clogger = this.getServer().getConsoleSender();
    @Getter
    private File projectFolder;

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

    public void onStart() {

        PlayersRunnable.AddMinuteRunnable();
        PlayersRunnable.SetTodayUpdatedRunnable();

    }

    public Map<String, ProjectData> getProjects() {
        return PluginData.getProjectdata();
    }

}
