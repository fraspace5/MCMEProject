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
import com.mcme.mcmeproject.util.DynmapUtil;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
    private boolean manager;

    private boolean head;

    @Override
    protected void execute(CommandSender cs, final String... args) {

        Player pl = (Player) cs;
        final Location loc = pl.getLocation();
        if (cs instanceof Player) {
            head = false;
            manager = false;
            if (PluginData.projectsAll.containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    if (PluginData.regions.containsKey(args[1]) && PluginData.regions.get(args[1]).idproject.equals(PluginData.projectsAll.get(args[0]).idproject)) {
                        if (PluginData.regions.get(args[1]).isInside(loc)) {
                            String n = args[1].toUpperCase() + " (" + args[0].toLowerCase() + ")";
                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {
                                        if (PluginData.warps.containsKey(PluginData.regions.get(args[1]).idr)) {
                                            String stat2 = "DELETE " + Mcproject.getPluginInstance().database + ".warps_data WHERE idregion = '" + PluginData.regions.get(args[1]).idr.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);

                                            DynmapUtil.deleteWarp(n);
                                        }

                                        String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".warps_data (idproject, idregion, world, server, x, y, z ) VALUES ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','" + PluginData.regions.get(args[1]).idr.toString() + "','" + loc.getWorld().getUID().toString() + "','" + Bukkit.getServer().getName() + "','" + loc.getX() + "','" + loc.getY() + "','" + loc.getZ() + "') ;";
                                        Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                        PluginData.loadWarps();
                                        //TODO SERVER LOADING
                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());

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

    public boolean playerPermission(final String prr, CommandSender cs) {
        final Player pl = (Player) cs;

        if (PluginData.projectsAll.get(prr).assistants.equals(pl.getUniqueId())) {
            manager = true;

        }
        if (PluginData.projectsAll.get(prr).head.equals(pl.getUniqueId())) {
            head = true;

        }

        if (manager || head || pl.hasPermission("project.owner")) {
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
