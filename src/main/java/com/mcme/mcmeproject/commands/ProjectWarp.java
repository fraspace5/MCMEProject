/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.DynmapUtil;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Fraspace5
 */
public class ProjectWarp extends ProjectCommand {

    public ProjectWarp(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Set a warp for a project region");
        setUsageDescription(" <ProjectName> <RegionName>: Set your current location as region warp location.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {

        Player pl = (Player) cs;
        Location loc = pl.getLocation();
        if (cs instanceof Player) {

            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    if (PluginData.getProjectdata().get(args[0]).regions.containsKey(args[1])) {
                        if (PluginData.getProjectdata().get(args[0]).regions.get(args[1]).isInside(loc)) {
                            PluginData.getProjectdata().get(args[0]).warps.put(args[1], loc);

                            String n = args[1].toUpperCase() + " (" + args[0].toLowerCase() + ")";

                            DynmapUtil.deleteWarp(n);

                            DynmapUtil.createMarkerWarp(n, loc);

                            sendDone(cs);

                        } else {
                            sendNoInside(cs);
                        }
                    }
                } else {

                    sendNoRegion(cs, args[1], args[0]);

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

    private void sendNoInside(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You aren't inside that region!");
    }

    private void sendNoRegion(CommandSender cs, String n, String p) {
        PluginData.getMessageUtil().sendErrorMessage(cs, n + " is not a region of " + p);
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Region warp updated!");
    }
}
