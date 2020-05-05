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

    private boolean manager;

    private boolean head;

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            manager = false;
            head = false;
            if (PluginData.projectsAll.containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    Player pl = (Player) cs;
                    try {
                        if (!args[1].equalsIgnoreCase("=") && !args[2].equalsIgnoreCase("=")) {
                            if (args[1].endsWith("%")) {
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {

                                        try {
                                            String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET percentage = '" + args[1].substring(0, args[1].length() - 1) + "', time = '" + setTime(args[2], cs).toString() + "', updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                            String stat2 = "DELETE FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_news_data WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);
                                            sendDone(cs, args[0]);
                                            PluginData.loadProjects();
                                            PluginData.setTodayEnd();
                                            Mcproject.getPluginInstance().sendReload(pl, "projects");
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
                                                String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET percentage = '" + args[1] + "', time = '" + setTime(args[2], cs).toString() + "', updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                                Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                                String stat2 = "DELETE FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_news_data WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                                Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);
                                                sendDone(cs, args[0]);
                                                PluginData.loadProjects();
                                                PluginData.setTodayEnd();
                                                Mcproject.getPluginInstance().sendReload(pl, "projects");
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
                                        String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET time = '" + setTime(args[2], cs).toString() + "', updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                        Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                        String stat2 = "DELETE FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_news_data WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                        Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);
                                        sendDone(cs, args[0]);
                                        PluginData.loadProjects();
                                        PluginData.setTodayEnd();
                                        Mcproject.getPluginInstance().sendReload(pl, "projects");
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
                                            String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET percentage = '" + args[1].substring(0, args[1].length() - 1) + "', updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                            String stat2 = "DELETE FROM  " + Mcproject.getPluginInstance().database + ".mcmeproject_news_data WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);
                                            sendDone(cs, args[0]);
                                            PluginData.loadProjects();
                                            PluginData.setTodayEnd();
                                            Mcproject.getPluginInstance().sendReload(pl, "projects");
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
                                                String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET percentage = '" + args[1] + "', updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                                Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                                String stat2 = "DELETE FROM  " + Mcproject.getPluginInstance().database + ".mcmeproject_news_data WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                                Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);
                                                sendDone(cs, args[0]);
                                                PluginData.loadProjects();
                                                PluginData.setTodayEnd();
                                                Mcproject.getPluginInstance().sendReload(pl, "projects");
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
                                        String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                        Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                        PluginData.loadProjects();
                                        sendDone(cs, args[0]);

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

    }

    public Long setTime(String t, CommandSender cs) {
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

    public boolean playerPermission(final String prr, CommandSender cs) {
        final Player pl = (Player) cs;

        if (PluginData.projectsAll.get(prr).assistants.contains(pl.getUniqueId())) {
            manager = true;

        }
        if (PluginData.projectsAll.get(prr).head.equals(pl.getUniqueId())) {
            head = true;

        }

        if (manager || head || pl.hasPermission("project.owner")) {
            return true;
        } else {
            sendNoPermission(cs);
            return false;
        }

    }

    private void sendNoPermission(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't manage this project");
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
