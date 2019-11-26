/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {

            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    try {
                        if (!args[1].equalsIgnoreCase("=") && !args[2].equalsIgnoreCase("=")) {

                            if (parseDouble(args[1]) > 100.0 || parseDouble(args[1]) < 0) {
                                sendNoPercentage(cs);
                            } else {
                                PluginData.getProjectdata().get(args[0]).percentage = parseDouble(args[1]);
                                setTime(args[2], cs, args[0]);
                                sendDone(cs, args[0]);
                                PluginData.getProjectdata().get(args[0]).news.clear();
                                PluginData.getProjectdata().get(args[0]).updated = System.currentTimeMillis();
                            }

                        } else if (args[1].equalsIgnoreCase("=") && !args[2].equalsIgnoreCase("=")) {

                            setTime(args[2], cs, args[0]);
                            sendDone(cs, args[0]);
                            PluginData.getProjectdata().get(args[0]).updated = System.currentTimeMillis();
                            PluginData.getProjectdata().get(args[0]).news.clear();

                        } else if (!args[1].equalsIgnoreCase("=") && args[2].equalsIgnoreCase("=")) {
                            if (parseDouble(args[1]) > 100.0 || parseDouble(args[1]) < 0) {
                                sendNoPercentage(cs);
                            } else {
                                PluginData.getProjectdata().get(args[0]).percentage = parseDouble(args[1]);
                                sendDone(cs, args[0]);
                                PluginData.getProjectdata().get(args[0]).updated = System.currentTimeMillis();
                                PluginData.getProjectdata().get(args[0]).news.clear();
                            }
                        } else if (args[1].equalsIgnoreCase("=") && args[2].equalsIgnoreCase("=")) {
                            sendDone(cs, args[0]);
                            PluginData.getProjectdata().get(args[0]).updated = System.currentTimeMillis();
                            PluginData.getProjectdata().get(args[0]).news.clear();
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

    public void setTime(String t, CommandSender cs, String nameProject) {
        String tt = t.substring(0, t.length() - 1);
        if (t.endsWith("y")) {

            Long r = 86400000 * (365 * parseLong(tt)) + System.currentTimeMillis();
            PluginData.getProjectdata().get(nameProject).time = r;

            //years 365 days
        } else if (t.endsWith("m")) {
            Long r = 86400000 * (31 * parseLong(tt)) + System.currentTimeMillis();
            PluginData.getProjectdata().get(nameProject).time = r;

//month 31 days
        } else if (t.endsWith("w")) {

            Long r = 86400000 * (7 * parseLong(tt)) + System.currentTimeMillis();
            PluginData.getProjectdata().get(nameProject).time = r;
//week 7 days
        } else if (t.endsWith("d")) {

            Long r = 86400000 * parseLong(tt) + System.currentTimeMillis();
            PluginData.getProjectdata().get(nameProject).time = r;

//days
        } else {

            sendNoTime(cs);
        }

    }

   public boolean playerPermission(String prr, CommandSender cs) {
        ProjectData pr = PluginData.getProjectdata().get(prr);
        Player pl = (Player) cs;
        if (pr.head.equals(pl.getUniqueId()) || pr.managers.contains(pl.getName()) || pl.hasPermission("project.owner")) {
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
