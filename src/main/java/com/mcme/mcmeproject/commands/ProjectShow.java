/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.ProjectStatus;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Fraspace5
 */
public class ProjectShow extends ProjectCommand {

    public ProjectShow(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Show the project in the project list");
        setUsageDescription(" <ProjectName> : Show the project in the project list");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {

            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    if (PluginData.getProjectdata().get(args[0]).status.equals(ProjectStatus.HIDDEN)) {
                        PluginData.getProjectdata().get(args[0]).status = ProjectStatus.SHOWED;
                        sendDone(cs);
                    } else if (PluginData.getProjectdata().get(args[0]).status.equals(ProjectStatus.SHOWED)) {
                        sendAlreadyVisible(cs);
                    } else {
                        sendFinished(cs);
                    }
                }
            } else {

                sendNoProject(cs);

            }

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
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendAlreadyVisible(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is already visible in the project list");
    }

    private void sendFinished(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is marked as finished! You can't show it in the list...");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "The project is visible");
    }

}
