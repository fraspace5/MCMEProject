/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author simonagottardi
 */
public class ProjectName extends ProjectCommand {

    public ProjectName(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Change the name of a project");
        setUsageDescription(" <OldProjectName> <NewProjectName>: Change the projectname of a project.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {

            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    if (!args[0].equals(args[1])) {

                        ProjectData p = PluginData.getProjectdata().get(args[0]);
                        p.name = args[1];
                        PluginData.getProjectdata().put(args[0], p);
                        PluginData.getProjectdata().remove(args[0]);

                        sendDone(cs, args[1]);
                    } else {
                        sendEqual(cs);
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
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendEqual(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Equal name");
    }

    private void sendDone(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Project name updated with " + name);
    }
}
