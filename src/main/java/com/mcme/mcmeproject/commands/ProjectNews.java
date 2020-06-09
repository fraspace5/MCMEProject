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
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectNews extends ProjectCommand {

    public ProjectNews(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Get or not news about project update");
        setUsageDescription(" true|false ");
    }

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        final Player pl = (Player) cs;

        if (args[0].equals("true") || args[0].equals("false")) {
            new BukkitRunnable() {

                @Override
                public void run() {

                    try {
                        String statement = "SELECT * FROM mcmeproject_news_bool WHERE player_uuid = '" + pl.getUniqueId().toString() + "' ;";
                        Statement statm1 = Mcproject.getPluginInstance().getConnection().prepareStatement(statement);
                        statm1.setQueryTimeout(10);
                        final ResultSet r = statm1.executeQuery(statement);

                        if (r.first()) {
                            switch (args[0]) {
                                case "true":

                                    if (r.getBoolean("bool") == false) {
                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {

                                                try {
                                                    String stat = "UPDATE mcmeproject_news_bool SET bool = 1 WHERE player_uuid = '" + pl.getUniqueId().toString() + "' ;";
                                                    Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                                    statm.setQueryTimeout(10);
                                                    statm.executeUpdate(stat);
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
                                                    String stat = "UPDATE mcmeproject_news_bool SET bool = 0 WHERE player_uuid = '" + pl.getUniqueId().toString() + "' ;";
                                                    Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                                    statm.setQueryTimeout(10);
                                                    statm.executeUpdate(stat);
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
                                                String stat = "UPDATE mcmeproject_news_bool SET bool = 0 WHERE player_uuid = '" + pl.getUniqueId().toString() + "' ;";
                                                Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                                statm.setQueryTimeout(10);
                                                statm.executeUpdate(stat);
                                            } catch (SQLException ex) {
                                                Logger.getLogger(ProjectNews.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                        }

                                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                                    break;

                            }

                        } else {

                            switch (args[0]) {
                                case "true":
                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {

                                            try {
                                                String stat = "INSERT INTO mcmeproject_news_bool (bool, player_uuid) VALUES(1,'" + pl.getUniqueId().toString() + "');";
                                                Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                                statm.setQueryTimeout(10);
                                                statm.executeUpdate(stat);
                                                sendDone(cs);
                                            } catch (SQLException ex) {
                                                Logger.getLogger(ProjectNews.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                        }

                                    }.runTaskAsynchronously(Mcproject.getPluginInstance());
                                    break;
                                case "false":
                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {

                                            try {
                                                String stat = "INSERT INTO mcmeproject_news_bool (bool, player_uuid) VALUES(0,'" + pl.getUniqueId().toString() + "');";
                                                Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                                statm.setQueryTimeout(10);
                                                statm.executeUpdate(stat);
                                                sendDone(cs);
                                            } catch (SQLException ex) {
                                                Logger.getLogger(ProjectNews.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                        }

                                    }.runTaskAsynchronously(Mcproject.getPluginInstance());
                                    break;

                            }

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
