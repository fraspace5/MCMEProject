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
public class ProjectHead extends ProjectCommand {

    public ProjectHead(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Sets the Head Project");
        setUsageDescription(" <ProjectName> <PlayerName>: Set <PlayerName> as Head project  ");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {

            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    try {
                        if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {

                            OfflinePlayer n = Bukkit.getOfflinePlayer(args[1]);
                            UUID uuid = n.getUniqueId();
                            PluginData.getProjectdata().get(args[0]).head = uuid;
                            sendDone(cs);
                        } else {
                            sendNoPlayer(cs);
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
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendNoPlayer(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid Player");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Head Project updated!");
    }
}
