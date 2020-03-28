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

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcmiddleearth.connect.util.ConnectUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
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

        if (cs instanceof Player) {
            Player pl = (Player) cs;
            if (PluginData.projectsAll.containsKey(args[0])) {
                if (args.length < 2 || args.length > 2) {

                    sendArgument(cs);

                } else {
                    if (PluginData.regionsReadable.get(PluginData.projectsAll.get(args[0]).idproject).contains(args[1])) {

                        if (PluginData.warps.containsKey(PluginData.regions.get(args[1]).idr)) {

                            if (Mcproject.getPluginInstance().nameserver.equals(PluginData.warps.get(PluginData.regions.get(args[1]).idr).server)) {
                                ProjectData p = PluginData.projectsAll.get(args[0]);
                                Location loc = PluginData.warps.get(PluginData.regions.get(args[1]).idr).location;

                                while (!(loc.getBlock().isPassable() && loc.getBlock().getRelative(BlockFace.UP).isPassable())) {

                                    loc = loc.getBlock().getRelative(BlockFace.UP).getLocation();

                                }
                                pl.teleport(loc);
                                FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                message.addSimple("Welcome " + pl.getName() + " in the area of : " + ChatColor.RED + p.name.toUpperCase() + " project");
                                if (PluginData.regionsReadable.get(PluginData.projectsAll.get(args[0]).idproject).size() != 1) {
                                    message.addSimple("\n" + ChatColor.GREEN + "The area name is: " + ChatColor.RED + (PluginData.regions.get(args[1]).name));
                                }

                                if (PluginData.informedRegion.containsKey(PluginData.regions.get(args[1]).idr)) {
                                    if (!PluginData.informedRegion.get(PluginData.regions.get(args[1]).idr).contains(pl.getUniqueId())) {
                                        message.send(pl);
                                        PluginData.informedRegion.get(PluginData.regions.get(args[1]).idr).add(pl.getUniqueId());
                                    }
                                } else {
                                    List<UUID> l = new ArrayList();
                                    PluginData.informedRegion.put(PluginData.regions.get(args[1]).idr, l);
                                    message.send(pl);
                                }

                            } else {

                                Location loc = PluginData.warps.get(PluginData.regions.get(args[1]).idr).location;

                                while (!(loc.getBlock().isPassable() && loc.getBlock().getRelative(BlockFace.UP).isPassable())) {

                                    loc = loc.getBlock().getRelative(BlockFace.UP).getLocation();

                                }

                                ConnectUtil.teleportPlayer(pl, PluginData.warps.get(PluginData.regions.get(args[1]).idr).server, PluginData.warps.get(PluginData.regions.get(args[1]).idr).wl.getName(), loc);
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
