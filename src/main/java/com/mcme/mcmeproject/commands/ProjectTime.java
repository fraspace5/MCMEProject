/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Fraspace5
 */
public class ProjectTime extends ProjectCommand {

    public ProjectTime(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Set the estimated time for the finish of the project");
        setUsageDescription(" <ProjectName> <ExtimatedTime>: Change time");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {

            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    setTime(args[1], cs, args[0]);
                    sendDone(cs);

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

        
        
        //cambia perchè ritorni un valore
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

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Time updated!");
    }

    private void sendNoTime(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Error with the time value!");
    }
}
