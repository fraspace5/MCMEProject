/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.util.ProjectStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author fraspace5
 */
public class ProjectNews extends ProjectCommand {

    public ProjectNews(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Get or not news about project update");
        setUsageDescription(" true|false ");
    }

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            final Player pl = (Player) cs;

            if (args[0].equals("true") || args[0].equals("false")) {
                new BukkitRunnable() {

                    @Override
                    public void run() {

                        try {
                            String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".news_bool WHERE player_uuid = '" + pl.getUniqueId().toString() + "' ;";
                            final ResultSet r = Mcproject.getPluginInstance().con.createStatement().executeQuery(statement);
                            switch (args[0]) {
                                case "true":

                                    if (r.getBoolean("bool") == false) {
                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {

                                                try {
                                                    String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".news_bool SET bool = true WHERE player_uuid = '" + pl.getUniqueId().toString() + "' ;";
                                                    Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                                } catch (SQLException ex) {
                                                    Logger.getLogger(ProjectNews.class.getName()).log(Level.SEVERE, null, ex);
                                                }

                                            }

                                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

                                        sendDone(cs);
                                    } else {
                                        sendTrue(cs);
                                    }

                                    break;

                                case "false":
                                    if (r.getBoolean("bool") == true) {

                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {

                                                try {
                                                    String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".news_bool SET bool = false WHERE player_uuid = '" + pl.getUniqueId().toString() + "' ;";
                                                    Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                                } catch (SQLException ex) {
                                                    Logger.getLogger(ProjectNews.class.getName()).log(Level.SEVERE, null, ex);
                                                }

                                            }

                                        }.runTaskAsynchronously(Mcproject.getPluginInstance());
                                        sendDone(cs);
                                    } else {
                                        sendFalse(cs);
                                    }
                                    break;
                                default:

                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {

                                            try {
                                                String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".news_bool SET bool = false WHERE player_uuid = '" + pl.getUniqueId().toString() + "' ;";
                                                Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                            } catch (SQLException ex) {
                                                Logger.getLogger(ProjectNews.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                        }

                                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                                    break;

                            }

                        } catch (SQLException ex) {
                            Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

            } else {
                sendError(cs);
            }

        }

    }

    private void sendTrue(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "It is already set as true!");
    }

    private void sendError(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Should be true or false!");
    }

    private void sendFalse(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "It is already set as false!");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Done!");
    }
}
