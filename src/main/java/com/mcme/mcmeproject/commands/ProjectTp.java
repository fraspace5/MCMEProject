/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (PluginData.getProjectdata().get(args[0]).warps.size() > 0) {
                    if (PluginData.getProjectdata().get(args[0]).warps.size() == 1) {

                        Location loc = PluginData.getProjectdata().get(args[0]).warps.get(args[1]);
                        pl.teleport(loc);

                    } else {

                        if (args.length < 2 || args.length > 2) {

                            sendArgument(cs);

                        } else {

                            if (!PluginData.getProjectdata().containsKey(args[1])) {
                                sendNoRegion(cs);
                            } else {

                                Location loc = PluginData.getProjectdata().get(args[0]).warps.get(args[1]);
                                pl.teleport(loc);

                            }

                        }

                    }

                } else {

                    sendNoTp(cs);
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
