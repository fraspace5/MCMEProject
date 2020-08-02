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
import com.mcme.mcmeproject.util.bungee;
import com.mcme.mcmeproject.util.utils;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectLocation extends ProjectCommand {

    public ProjectLocation(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Set a warp for a project region");
        setUsageDescription(" <ProjectName> <RegionName>: Set your current location as region warp location.");
    }

    @Override
    protected void execute(CommandSender cs, final String... args) {

        Player pl = (Player) cs;
        final Location loc = pl.getLocation();

        if (PluginData.getProjectsAll().containsKey(args[0])) {

            if (utils.playerPermission(args[0], cs)) {

                if (PluginData.getRegions().containsKey(args[1]) && PluginData.getRegions().get(args[1]).getIdproject().equals(PluginData.getProjectsAll().get(args[0]).getIdproject())) {

                    if (PluginData.getRegions().get(args[1]).isInside(loc)) {

                        String n = args[1].toUpperCase() + " (" + args[0].toLowerCase() + ")";

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    if (PluginData.getWarps().containsKey(PluginData.getRegions().get(args[1]).getIdr())) {

                                        Mcproject.getPluginInstance().getDeleteWarp().setString(1, PluginData.getRegions().get(args[1]).getIdr().toString());
                                        Mcproject.getPluginInstance().getDeleteWarp().executeUpdate();
                                        DynmapUtil.deleteWarp(n);
                                    }

                                    Mcproject.getPluginInstance().getInsertWarp().setString(1, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                    Mcproject.getPluginInstance().getInsertWarp().setString(2, PluginData.getRegions().get(args[1]).getIdr().toString());
                                    Mcproject.getPluginInstance().getInsertWarp().setString(3, loc.getWorld().getName());
                                    Mcproject.getPluginInstance().getInsertWarp().setString(4, Mcproject.getPluginInstance().getNameserver());
                                    Mcproject.getPluginInstance().getInsertWarp().setDouble(5, loc.getX());
                                    Mcproject.getPluginInstance().getInsertWarp().setDouble(6, loc.getY());
                                    Mcproject.getPluginInstance().getInsertWarp().setDouble(7, loc.getZ());

                                    Mcproject.getPluginInstance().getDeleteWarp().executeUpdate();
                                    PluginData.loadWarps();
                                    bungee.sendReload(pl, "warps");
                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

                        sendDone(cs);
                        DynmapUtil.createMarkerWarp(n, loc, loc.getWorld().getName());
                        PluginData.loadAllDynmap();
                        bungee.sendReload(pl, "map");

                    } else {
                        sendNoInside(cs);
                    }
                } else {

                    sendNoRegion(cs, args[1], args[0]);

                }
            }

        } else {

            sendNoProject(cs);

        }

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
