/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcmiddleearth.connect.ConnectPlugin;
import com.mcmiddleearth.connect.util.ConnectUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Fraspace5
 */
public class ProjectTp extends ProjectCommand {

    public ProjectTp(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Teleport to a project Region");
        setUsageDescription(" <ProjectName> <ProjectRegion>");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
//attenzione, usa quello di eriol che supporta il bungeecord così non ci sono problemi
        if (cs instanceof Player) {
            Player pl = (Player) cs;
            if (PluginData.projectsAll.containsKey(args[0])) {
                if (args.length < 2 || args.length > 2) {

                    sendArgument(cs);

                } else {
                    if (PluginData.regionsReadable.get(PluginData.projectsAll.get(args[0]).idproject).contains(args[1])) {

                        if (PluginData.warps.containsKey(PluginData.regions.get(args[1]).idr)) {
                            if (Bukkit.getServer().getName().equals(PluginData.warps.get(PluginData.regions.get(args[1]).idr).server)) {
                                pl.teleport(PluginData.warps.get(PluginData.regions.get(args[1]).idr).location);
                            } else {
                                ConnectUtil.teleportPlayer(pl, PluginData.warps.get(PluginData.regions.get(args[1]).idr).server, PluginData.warps.get(PluginData.regions.get(args[1]).idr).wl.getName(), PluginData.warps.get(PluginData.regions.get(args[1]).idr).location);
                            }
                        } else {

                            sendNoTp(cs);
                        }

                    } else {

                        sendNoRegion(cs);

                    }

                }

            } else {

                sendNoProject(cs);

            }

        }

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendNoRegion(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Region doesn't exists");
    }

    private void sendNoTp(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "No warp available for this region!");
    }

    private void sendManagerError(CommandSender cs, String n, String p) {
        PluginData.getMessageUtil().sendErrorMessage(cs, n + " is not a manager of " + p);
    }

    private void sendArgument(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, " Which region of the project, invalid command!");
    }

    private void sendManager(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Manager " + name + " removed!");
    }

}
