/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.ProjectStatus;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
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
        setUsageDescription(" <ProjectName> <Percentage> <ExtimatedTime>: Update the information of a project (Percentage and Extimated Time for finish work)");
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
                    try {
                        if (!args[1].equalsIgnoreCase("=") && !args[2].equalsIgnoreCase("=")) {

                            if (parseDouble(args[1]) > 100.0 || parseDouble(args[1]) < 0) {
                                sendNoPercentage(cs);
                            } else {

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {

                                        try {
                                            String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET percentage = '" + args[1] + "', time = '" + setTime(args[2], cs, args[0]) + "', updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                            String stat2 = "DELETE " + Mcproject.getPluginInstance().database + ".news_data WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);
                                            sendDone(cs, args[0]);
                                        } catch (SQLException ex) {
                                            Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }

                                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                            }

                        } else if (args[1].equalsIgnoreCase("=") && !args[2].equalsIgnoreCase("=")) {

                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {
                                        String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET time = '" + setTime(args[2], cs, args[0]) + "', updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                        Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                        String stat2 = "DELETE " + Mcproject.getPluginInstance().database + ".news_data WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                        Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);
                                        sendDone(cs, args[0]);
                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());

                        } else if (!args[1].equalsIgnoreCase("=") && args[2].equalsIgnoreCase("=")) {
                            if (parseDouble(args[1]) > 100.0 || parseDouble(args[1]) < 0) {
                                sendNoPercentage(cs);
                            } else {

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {

                                        try {
                                            String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET percentage = '" + args[1] + "', updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                            String stat2 = "DELETE " + Mcproject.getPluginInstance().database + ".news_data WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);
                                            sendDone(cs, args[0]);
                                        } catch (SQLException ex) {
                                            Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }

                                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                            }
                        } else if (args[1].equalsIgnoreCase("=") && args[2].equalsIgnoreCase("=")) {

                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {
                                        String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                        Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                        String stat2 = "DELETE " + Mcproject.getPluginInstance().database + ".news_data WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                        Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);
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

    public Long setTime(String t, CommandSender cs, String nameProject) {
        String tt = t.substring(0, t.length() - 1);
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

            Long r = 86400000 * parseLong(tt) + System.currentTimeMillis();
            return r;

//days
        } else {

            sendNoTime(cs);

            return null;
        }

    }

    public boolean playerPermission(final String prr, CommandSender cs) {
        final Player pl = (Player) cs;
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".staff_data WHERE idproject =" + PluginData.getProjectsAll().get(prr).idproject.toString() + " AND staff_uuid =" + pl.getUniqueId().toString() + " ;";

                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                    String st = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".project_data WHERE idproject =" + PluginData.getProjectsAll().get(prr).idproject.toString() + " ;";

                    final ResultSet r2 = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                    if (r.first()) {
                        manager = true;

                    }
                    if (UUID.fromString(r2.getString("staff_uuid")).equals(pl.getUniqueId())) {
                        head = true;

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ProjectAdd.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

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
        PluginData.getMessageUtil().sendErrorMessage(cs, "You have to use a numeric value!");
    }

}
