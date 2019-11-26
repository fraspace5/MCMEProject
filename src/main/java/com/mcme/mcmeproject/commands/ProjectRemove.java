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
 * @author Fraspace5
 */
public class ProjectRemove extends ProjectCommand {

    public ProjectRemove(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Remove a manager from the list");
        setUsageDescription(" <ProjectName> <PlayerName>: Remove a manager from a project");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {

            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {

                    try {
                        OfflinePlayer n = Bukkit.getOfflinePlayer(args[1]);
                        UUID uuid = n.getUniqueId();

                        if (PluginData.getProjectdata().get(args[0]).managers.contains(uuid)) {

                            ProjectData p = PluginData.getProjectdata().get(args[0]);
                            p.managers.remove(uuid);
                            sendManager(cs, args[1]);
                        } else {

                            sendManagerError(cs, args[1], args[0]);

                        }
                    } catch (NullPointerException e) {

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

    private void sendManagerError(CommandSender cs, String n, String p) {
        PluginData.getMessageUtil().sendErrorMessage(cs, n + " is not a manager of " + p);
    }

    private void sendManager(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Manager " + name + " removed!");
    }

}
