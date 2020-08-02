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
                        Mcproject.getPluginInstance().getSelectNewsBool().setString(1, pl.getUniqueId().toString());

                        final ResultSet r = Mcproject.getPluginInstance().getSelectNewsBool().executeQuery();

                        if (r.first()) {
                            switch (args[0]) {
                                case "true":

                                    if (r.getBoolean("bool") == false) {
                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {

                                                try {
                                                    Mcproject.getPluginInstance().getUpdateNews().setBoolean(1, true);
                                                    Mcproject.getPluginInstance().getUpdateNews().setString(2, pl.getUniqueId().toString());
                                                    Mcproject.getPluginInstance().getUpdateNews().executeUpdate();
                                                    sendDone(cs);
                                                } catch (SQLException ex) {
                                                    Logger.getLogger(ProjectNews.class.getName()).log(Level.SEVERE, null, ex);
                                                }

                                            }

                                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

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
                                                    Mcproject.getPluginInstance().getUpdateNews().setBoolean(1, false);
                                                    Mcproject.getPluginInstance().getUpdateNews().setString(2, pl.getUniqueId().toString());
                                                    Mcproject.getPluginInstance().getUpdateNews().executeUpdate();
                                                    sendDone(cs);
                                                } catch (SQLException ex) {
                                                    Logger.getLogger(ProjectNews.class.getName()).log(Level.SEVERE, null, ex);
                                                }

                                            }

                                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

                                    } else {
                                        sendFalse(cs);
                                    }
                                    break;
                                default:
                                    sendError(cs);
                                    break;

                            }

                        } else {
                            switch (args[0]) {
                                case "true":
                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {

                                            try {
                                                Mcproject.getPluginInstance().getInsertNewsBool().setBoolean(1, true);
                                                Mcproject.getPluginInstance().getInsertNewsBool().setString(2, pl.getUniqueId().toString());
                                                Mcproject.getPluginInstance().getInsertNewsBool().executeUpdate();
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
                                                Mcproject.getPluginInstance().getInsertNewsBool().setBoolean(0, true);
                                                Mcproject.getPluginInstance().getInsertNewsBool().setString(2, pl.getUniqueId().toString());
                                                Mcproject.getPluginInstance().getInsertNewsBool().executeUpdate();
                                                sendDone(cs);
                                            } catch (SQLException ex) {
                                                Logger.getLogger(ProjectNews.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                        }

                                    }.runTaskAsynchronously(Mcproject.getPluginInstance());
                                    break;
                                default:
                                    sendError(cs);
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
