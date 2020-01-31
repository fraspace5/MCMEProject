/*
 * Copyright (C) 2020 MCME (Fraspace5)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcmiddleearth.connect.util.ConnectUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Fraspace5
 */
public class ProjectWarp extends ProjectCommand {

    public ProjectWarp(String... permissionNodes) {
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
