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
import com.mcmiddleearth.pluginutil.region.CuboidRegion;
import com.mcmiddleearth.pluginutil.region.PrismoidRegion;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import static java.lang.Integer.parseInt;
import java.sql.Statement;

/**
 *
 * @author Fraspace5
 */
public class ProjectArea extends ProjectCommand {

    public ProjectArea(String... permissionNodes) {
        super(4, true, permissionNodes);
        setShortDescription(": You can add or remove one region from a project ");
        setUsageDescription(" <ProjectName> add|remove <RegionName> <weight>: Add or remove a region of a project");
    }

    private Region weRegion;

    @Override
    protected void execute(CommandSender cs, final String... args) {

        final Player pl = (Player) cs;
        final Location loc = pl.getLocation();

        if (PluginData.getProjectsAll().containsKey(args[0])) {
            if (utils.playerPermission(args[0], cs)) {
                if (args[1].equalsIgnoreCase("add")) {
                    List l = new ArrayList<>();
                    if (PluginData.getRegionsReadable().containsKey(PluginData.getProjectsAll().get(args[0]).getIdproject())) {
                        l = createList(PluginData.getProjectsAll().get(args[0]).getIdproject());
                    }
                    if (!l.contains(args[2].toLowerCase()) || l.isEmpty()) {
                        try {
                            WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

                            weRegion = worldEdit.getSession(pl).getSelection(worldEdit.getSession(pl).getSelectionWorld());

                            if (!(weRegion instanceof com.sk89q.worldedit.regions.CuboidRegion || weRegion instanceof Polygonal2DRegion)) {
                                sendInvalidSelection(pl);

                            } else if (weRegion instanceof Polygonal2DRegion) {

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {

                                        PrismoidRegion r = new PrismoidRegion(loc, (com.sk89q.worldedit.regions.Polygonal2DRegion) weRegion);
                                        try {
                                            String stat = "INSERT INTO mcmeproject_regions_data (idproject, idregion, name, type, xlist, zlist, ymin, ymax, location, server, weight ) VALUES ('" + PluginData.getProjectsAll().get(args[0]).getIdproject().toString() + "','" + utils.createId().toString() + "','" + args[2] + "','prismoid','" + serialize(r.getXPoints()) + "','" + serialize(r.getZPoints()) + "','" + r.getMinY() + "','" + r.getMaxY() + "','" + pl.getLocation().getWorld().getName() + ";" + pl.getLocation().getX() + ";" + pl.getLocation().getY() + ";" + pl.getLocation().getZ() + "','" + Mcproject.getPluginInstance().getNameserver() + "','" + parseInt(args[3]) + "' ) ;";
                                            Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                            statm.executeUpdate(stat);
                                            PluginData.loadRegions();
                                            PluginData.loadWarps();
                                            bungee.sendReload(pl, "regions");
                                            bungee.sendReload(pl, "warps");

                                            statm.setQueryTimeout(10);
                                        } catch (SQLException | NumberFormatException ex) {
                                            if (ex instanceof NumberFormatException) {
                                                PluginData.getMessageUtil().sendErrorMessage(cs, "It should be an integer number");
                                            } else if (ex instanceof SQLException) {
                                                Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                        }

                                    }

                                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                                try {
                                    DynmapUtil.createMarker(args[2], args[0], weRegion);
                                } catch (NullPointerException e) {

                                }

                                sendDone(cs);
                            } else if (weRegion instanceof com.sk89q.worldedit.regions.CuboidRegion) {

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {

                                        CuboidRegion r = new CuboidRegion(loc, (com.sk89q.worldedit.regions.CuboidRegion) weRegion);
                                        Vector minCorner = r.getMinCorner();
                                        Vector maxCorner = r.getMaxCorner();
                                        try {
                                            String stat = "INSERT INTO mcmeproject_regions_data (idproject, idregion, name, type, xlist, zlist, ymin, ymax, location, server, weight ) VALUES ('" + PluginData.getProjectsAll().get(args[0]).getIdproject().toString() + "','" + utils.createId().toString() + "','" + args[2] + "','cuboid','" + minCorner.getBlockX() + ";" + maxCorner.getBlockX() + "','" + minCorner.getBlockZ() + ";" + maxCorner.getBlockZ() + "','" + minCorner.getBlockY() + "','" + maxCorner.getBlockY() + "','" + pl.getLocation().getWorld().getName() + ";" + pl.getLocation().getX() + ";" + pl.getLocation().getY() + ";" + pl.getLocation().getZ() + "','" + Mcproject.getPluginInstance().getNameserver() + "','" + parseInt(args[3]) + "' ) ;";
                                            Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                            statm.executeUpdate(stat);
                                            PluginData.loadRegions();
                                            PluginData.loadWarps();
                                            bungee.sendReload(pl, "regions");
                                            bungee.sendReload(pl, "warps");
                                            statm.setQueryTimeout(10);
                                        } catch (SQLException | NumberFormatException ex) {
                                            if (ex instanceof NumberFormatException) {
                                                PluginData.getMessageUtil().sendErrorMessage(cs, "It should be an integer number");
                                            } else if (ex instanceof SQLException) {
                                                Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                        }

                                    }

                                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                                try {
                                    DynmapUtil.createMarker(args[2], args[0], weRegion);
                                } catch (NullPointerException e) {

                                }

                                sendDone(cs);
                            }
                        } catch (IncompleteRegionException | NullPointerException ex) {

                            Logger.getLogger(ProjectArea.class.getName()).log(Level.SEVERE, null, ex);
                            if (ex instanceof NullPointerException) {
                                sendInvalidSelection(pl);
                            } else if (ex instanceof IncompleteRegionException) {
                                sendInvalidSelection(pl);

                            }
                        }

                    } else {
                        sendRegion(cs, args[2], args[0]);
                    }
                } else {
                    if (PluginData.getRegionsReadable().containsKey(PluginData.getProjectsAll().get(args[0]).getIdproject())) {
                        if (PluginData.getRegionsReadable().get(PluginData.getProjectsAll().get(args[0]).getIdproject()).contains(args[2])) {

                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {
                                        String stat = "DELETE FROM mcmeproject_regions_data WHERE idregion = '" + PluginData.getRegions().get(args[2]).getIdr().toString() + "' ;";
                                        Statement statm1 = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                        statm1.executeUpdate(stat);
                                        String stat2 = "DELETE FROM mcmeproject_warps_data WHERE idregion = '" + PluginData.getRegions().get(args[2]).getIdr().toString() + "' ;";
                                        Statement statm2 = Mcproject.getPluginInstance().getConnection().prepareStatement(stat2);
                                        statm2.executeUpdate(stat);
                                        statm1.setQueryTimeout(10);
                                        statm2.setQueryTimeout(10);

                                        sendDel(cs);
                                        PluginData.loadRegions();
                                        PluginData.loadWarps();
                                        bungee.sendReload(pl, "regions");
                                        bungee.sendReload(pl, "warps");

                                        try {
                                            DynmapUtil.removeMarker(args[2]);
                                            String n = args[2].toUpperCase() + " (" + args[0].toLowerCase() + ")" + ".marker";
                                            DynmapUtil.deleteWarp(n);
                                            PluginData.loadAllDynmap();
                                            bungee.sendReload(pl, "map");

                                        } catch (NullPointerException e) {
                                        }
                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectArea.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());

                        } else {

                            sendRegionDel(cs, args[2], args[0]);

                        }
                    } else {

                        sendRegionDel(cs, args[2], args[0]);

                    }
                }
            }

        } else {

            sendNoProject(cs);

        }

    }

    private String serialize(Integer[] intlist) {

        StringBuilder builder = new StringBuilder();

        for (Integer intlist1 : intlist) {
            builder.append(String.valueOf(intlist1)).append(";");
        }

        return builder.toString();

    }

    private static List<String> createList(UUID id) {
        List<String> s = new ArrayList<>();

        PluginData.getRegionsReadable().get(id).forEach((regions) -> {
            s.add(regions.toLowerCase());
        });
        return s;
    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendRegion(CommandSender cs, String n, String p) {
        PluginData.getMessageUtil().sendErrorMessage(cs, n + " is a region of " + p);
    }

    private void sendRegionDel(CommandSender cs, String n, String p) {
        PluginData.getMessageUtil().sendErrorMessage(cs, n + " is not a region of " + p);
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Region updated!");

    }

    private void sendDel(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Region deleted!");

    }

    private void sendInvalidSelection(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "For a cuboid or polygonal area make a valid WorldEdit selection first.");
    }

}
