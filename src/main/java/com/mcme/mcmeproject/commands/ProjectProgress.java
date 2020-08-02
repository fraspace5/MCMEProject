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
import com.mcme.mcmeproject.util.bungee;
import com.mcme.mcmeproject.util.utils;
import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
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
public class ProjectProgress extends ProjectCommand {

    public ProjectProgress(String... permissionNodes) {
        super(3, true, permissionNodes);
        setShortDescription(": Simple command to update a project");
        setUsageDescription(" <ProjectName> <Percentage> <EstimatedTime>: Update the information of a project (Percentage and Estimated Time for finish work)");
    }
    //extimated time y/m/w/d

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (PluginData.getProjectsAll().containsKey(args[0])) {
            if (utils.playerPermission(args[0], cs)) {
                Player pl = (Player) cs;
                try {
                    if (!args[1].equalsIgnoreCase("=") && !args[2].equalsIgnoreCase("=")) {
                        if (args[1].endsWith("%")) {
                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {

                                        Mcproject.getPluginInstance().getUpdateProgress().setString(1, args[1].substring(0, args[1].length() - 1));
                                        Mcproject.getPluginInstance().getUpdateProgress().setString(2, setTime(args[2], cs).toString());
                                        Mcproject.getPluginInstance().getUpdateProgress().setLong(3, System.currentTimeMillis());
                                        Mcproject.getPluginInstance().getUpdateProgress().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                        Mcproject.getPluginInstance().getUpdateProgress().executeUpdate();

                                        Mcproject.getPluginInstance().getDeleteNewsData().setString(1, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                        Mcproject.getPluginInstance().getDeleteNewsData().executeUpdate();
                                        sendDone(cs, args[0]);
                                        PluginData.loadProjects();
                                        PluginData.setTodayEnd();
                                        bungee.sendReload(pl, "projects");
                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());
                        } else {
                            if (parseDouble(args[1]) > 100.0 || parseDouble(args[1]) < 0) {
                                sendNoPercentage(cs);
                            } else {

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {

                                        try {

                                            Mcproject.getPluginInstance().getUpdateProgress().setString(1, args[1]);
                                            Mcproject.getPluginInstance().getUpdateProgress().setString(2, setTime(args[2], cs).toString());
                                            Mcproject.getPluginInstance().getUpdateProgress().setLong(3, System.currentTimeMillis());
                                            Mcproject.getPluginInstance().getUpdateProgress().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                            Mcproject.getPluginInstance().getUpdateProgress().executeUpdate();

                                            Mcproject.getPluginInstance().getDeleteNewsData().setString(1, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                            Mcproject.getPluginInstance().getDeleteNewsData().executeUpdate();
                                            sendDone(cs, args[0]);
                                            PluginData.loadProjects();
                                            PluginData.setTodayEnd();
                                            bungee.sendReload(pl, "projects");
                                        } catch (SQLException ex) {
                                            Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }

                                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                            }
                        }
                    } else if (args[1].equalsIgnoreCase("=") && !args[2].equalsIgnoreCase("=")) {

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {

                                    Mcproject.getPluginInstance().getUpdateInformations().setString(1, "time");
                                    Mcproject.getPluginInstance().getUpdateInformations().setString(2, setTime(args[2], cs).toString());
                                    Mcproject.getPluginInstance().getUpdateInformations().setLong(3, System.currentTimeMillis());
                                    Mcproject.getPluginInstance().getUpdateInformations().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                    Mcproject.getPluginInstance().getUpdateInformations().executeUpdate();

                                    Mcproject.getPluginInstance().getDeleteNewsData().setString(1, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                    Mcproject.getPluginInstance().getDeleteNewsData().executeUpdate();
                                    sendDone(cs, args[0]);
                                    PluginData.loadProjects();
                                    PluginData.setTodayEnd();
                                    bungee.sendReload(pl, "projects");
                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    } else if (!args[1].equalsIgnoreCase("=") && args[2].equalsIgnoreCase("=")) {
                        if (args[1].endsWith("%")) {

                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {

                                        Mcproject.getPluginInstance().getUpdateInformations().setString(1, "percentage");
                                        Mcproject.getPluginInstance().getUpdateInformations().setString(2, args[1].substring(0, args[1].length() - 1));
                                        Mcproject.getPluginInstance().getUpdateInformations().setLong(3, System.currentTimeMillis());
                                        Mcproject.getPluginInstance().getUpdateInformations().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                        Mcproject.getPluginInstance().getUpdateInformations().executeUpdate();

                                        Mcproject.getPluginInstance().getDeleteNewsData().setString(1, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                        Mcproject.getPluginInstance().getDeleteNewsData().executeUpdate();
                                        sendDone(cs, args[0]);
                                        PluginData.loadProjects();
                                        PluginData.setTodayEnd();
                                        bungee.sendReload(pl, "projects");
                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());
                        } else {

                            if (parseDouble(args[1]) > 100.0 || parseDouble(args[1]) < 0) {
                                sendNoPercentage(cs);
                            } else {

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {

                                        try {
                                            String stat = "UPDATE mcmeproject_project_data SET percentage = '" + args[1] + "', updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.getProjectsAll().get(args[0]).getIdproject().toString() + "' ;";

                                            Mcproject.getPluginInstance().getUpdateInformations().setString(1, "percentage");
                                            Mcproject.getPluginInstance().getUpdateInformations().setString(2, args[1]);
                                            Mcproject.getPluginInstance().getUpdateInformations().setLong(3, System.currentTimeMillis());
                                            Mcproject.getPluginInstance().getUpdateInformations().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                            Mcproject.getPluginInstance().getUpdateInformations().executeUpdate();

                                            Mcproject.getPluginInstance().getDeleteNewsData().setString(1, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                            Mcproject.getPluginInstance().getDeleteNewsData().executeUpdate();
                                            sendDone(cs, args[0]);
                                            PluginData.loadProjects();
                                            PluginData.setTodayEnd();
                                            bungee.sendReload(pl, "projects");
                                        } catch (SQLException ex) {
                                            Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }

                                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                            }
                        }
                    } else if (args[1].equalsIgnoreCase("=") && args[2].equalsIgnoreCase("=")) {

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {

                                    Mcproject.getPluginInstance().getUpdateWithoutUpdated().setString(1, "percentage");
                                    Mcproject.getPluginInstance().getUpdateWithoutUpdated().setLong(2, System.currentTimeMillis());
                                    Mcproject.getPluginInstance().getUpdateWithoutUpdated().setString(3, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                    Mcproject.getPluginInstance().getUpdateWithoutUpdated().executeUpdate();
                                    PluginData.loadProjects();
                                    sendDone(cs, args[0]);
                                    bungee.sendReload(pl, "projects");
                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());
                    }

                } catch (NumberFormatException | NullPointerException nfe) {
                    sendNoNumber(cs);
                }
            }
        } else {

            sendNoProject(cs);

        }

    }

    private Long setTime(String t, CommandSender cs) {
        String tt = t.substring(0, t.length() - 1);
        try {
            if (t.endsWith("y")) {

                Long r = 86400000 * (365 * parseLong(tt)) + System.currentTimeMillis();
                return r;

                //years 365 days
            } else if (t.endsWith("m")) {

                Long r = 86400000 * (31 * parseLong(tt)) + System.currentTimeMillis();

                return r;

//month 31 days
            } else if (t.endsWith("w")) {

                Long r = 86400000 * (7 * parseLong(tt)) + System.currentTimeMillis();
                return r;
//week 7 days
            } else if (t.endsWith("d")) {

                Long r = (86400000 * parseLong(tt)) + System.currentTimeMillis();
                return r;

//days
            } else {

                sendNoTime(cs);

                return null;
            }
        } catch (NumberFormatException e) {
            sendNoTime(cs);
            return null;
        }

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendNoTime(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Error with the time value!");
    }

    private void sendDone(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, name + " Project updated!");
    }

    private void sendNoPercentage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Percentage value should be less than 100 and more than 0");
    }

    private void sendNoNumber(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You have to use a numeric value(only integers)!");
    }

}
