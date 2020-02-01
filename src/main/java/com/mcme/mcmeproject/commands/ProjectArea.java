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
import com.mcmiddleearth.pluginutil.region.CuboidRegion;
import com.mcmiddleearth.pluginutil.region.PrismoidRegion;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Fraspace5
 */
public class ProjectArea extends ProjectCommand {

    public Region weRegion;

    public ProjectArea(String... permissionNodes) {
        super(3, true, permissionNodes);
        setShortDescription(": Iterate with the region of a project ");
        setUsageDescription(" <ProjectName> add|remove <RegionName>: Add or remove a region of a project");
    }

    private boolean manager;

    private boolean head;

    @Override
    protected void execute(CommandSender cs, final String... args) {

        final Player pl = (Player) cs;
        final Location loc = pl.getLocation();
        if (cs instanceof Player) {
            head = false;
            manager = false;
            if (PluginData.projectsAll.containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    if (args[1].equalsIgnoreCase("add")) {

                        if (PluginData.regionsReadable.containsKey(PluginData.projectsAll.get(args[0]).idproject)) {
                            if (!PluginData.regionsReadable.get(PluginData.projectsAll.get(args[0]).idproject).contains(args[2])) {
                                WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

                                try {
                                    weRegion = worldEdit.getSession(pl).getSelection(worldEdit.getSession(pl).getSelectionWorld());
                                } catch (IncompleteRegionException ex) {
                                    Logger.getLogger(ProjectArea.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                if (!(weRegion instanceof com.sk89q.worldedit.regions.CuboidRegion || weRegion instanceof Polygonal2DRegion)) {
                                    sendInvalidSelection(pl);

                                } else if (weRegion instanceof Polygonal2DRegion) {

                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {

                                            try {
                                                PrismoidRegion r = new PrismoidRegion(loc, (com.sk89q.worldedit.regions.Polygonal2DRegion) weRegion);

                                                String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".regions_data (idproject, idregion, name, type, xlist, zlist, ymin, ymax, location, server ) VALUES ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','" + PluginData.createId().toString() + "','" + args[2] + "','prismoid','" + serialize(r.getXPoints()) + "','" + serialize(r.getZPoints()) + "','" + r.getMinY() + "','" + r.getMaxY() + "','" + pl.getLocation().getWorld().getUID().toString() + ";" + pl.getLocation().getX() + ";" + pl.getLocation().getY() + ";" + pl.getLocation().getZ() + "','" + Mcproject.getPluginInstance().nameserver + "' ) ;";
                                                Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                                PluginData.loadRegions();
                                                PluginData.loadWarps();
                                                Mcproject.getPluginInstance().sendReload(pl, "regions");
                                                Mcproject.getPluginInstance().sendReload(pl, "warps");

                                            } catch (SQLException ex) {
                                                Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
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

                                            try {
                                                CuboidRegion r = new CuboidRegion(loc, (com.sk89q.worldedit.regions.CuboidRegion) weRegion);
                                                Vector minCorner = r.getMinCorner();
                                                Vector maxCorner = r.getMaxCorner();

                                                String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".regions_data (idproject, idregion, name, type, xlist, zlist, ymin, ymax, location, server ) VALUES ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','" + PluginData.createId().toString() + "','" + args[2] + "','cuboid','" + minCorner.getBlockX() + ";" + maxCorner.getBlockX() + "','" + minCorner.getBlockZ() + ";" + maxCorner.getBlockZ() + "','" + minCorner.getBlockY() + "','" + maxCorner.getBlockY() + "','" + pl.getLocation().getWorld().getUID().toString() + ";" + pl.getLocation().getX() + ";" + pl.getLocation().getY() + ";" + pl.getLocation().getZ() + "','" + Mcproject.getPluginInstance().nameserver + "' ) ;";
                                                Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                                PluginData.loadRegions();
                                                PluginData.loadWarps();
                                                Mcproject.getPluginInstance().sendReload(pl, "regions");
                                                Mcproject.getPluginInstance().sendReload(pl, "warps");

                                            } catch (SQLException ex) {
                                                Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                        }

                                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                                    try {
                                        DynmapUtil.createMarker(args[2], args[0], weRegion);
                                    } catch (NullPointerException e) {

                                    }

                                    sendDone(cs);
                                }

                            } else {
                                sendRegion(cs, args[2], args[0]);

                            }
                        } else {

                            WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

                            try {
                                weRegion = worldEdit.getSession(pl).getSelection(worldEdit.getSession(pl).getSelectionWorld());
                            } catch (IncompleteRegionException ex) {
                                Logger.getLogger(ProjectArea.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            if (!(weRegion instanceof com.sk89q.worldedit.regions.CuboidRegion || weRegion instanceof Polygonal2DRegion)) {
                                sendInvalidSelection(pl);

                            } else if (weRegion instanceof Polygonal2DRegion) {

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {

                                        try {
                                            PrismoidRegion r = new PrismoidRegion(loc, (com.sk89q.worldedit.regions.Polygonal2DRegion) weRegion);

                                            String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".regions_data (idproject, idregion, name, type, xlist, zlist, ymin, ymax, location, server ) VALUES ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','" + PluginData.createId().toString() + "','" + args[2] + "','prismoid','" + serialize(r.getXPoints()) + "','" + serialize(r.getZPoints()) + "','" + r.getMinY() + "','" + r.getMaxY() + "','" + pl.getLocation().getWorld().getUID().toString() + ";" + pl.getLocation().getX() + ";" + pl.getLocation().getY() + ";" + pl.getLocation().getZ() + "','" + Mcproject.getPluginInstance().nameserver + "' ) ;";
                                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                            PluginData.loadRegions();
                                            PluginData.loadWarps();

                                            Mcproject.getPluginInstance().sendReload(pl, "regions");
                                            Mcproject.getPluginInstance().sendReload(pl, "warps");
                                        } catch (SQLException ex) {
                                            Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
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

                                        try {
                                            CuboidRegion r = new CuboidRegion(loc, (com.sk89q.worldedit.regions.CuboidRegion) weRegion);
                                            Vector minCorner = r.getMinCorner();
                                            Vector maxCorner = r.getMaxCorner();

                                            String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".regions_data (idproject, idregion, name, type, xlist, zlist, ymin, ymax, location, server ) VALUES"
                                                    + " ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','"
                                                    + PluginData.createId().toString() + "','" + args[2] + "','cuboid','" + minCorner.getBlockX() + ";" + maxCorner.getBlockX() + "','" + minCorner.getBlockZ() + ";" + maxCorner.getBlockZ() + "','" + minCorner.getBlockY() + "','" + maxCorner.getBlockY() + "','" + pl.getLocation().getWorld().getUID().toString() + ";" + pl.getLocation().getX() + ";" + pl.getLocation().getY() + ";" + pl.getLocation().getZ() + "','" + Mcproject.getPluginInstance().nameserver + "' ) ;";
                                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                            PluginData.loadRegions();
                                            PluginData.loadWarps();

                                            Mcproject.getPluginInstance().sendReload(pl, "regions");
                                            Mcproject.getPluginInstance().sendReload(pl, "warps");
                                        } catch (SQLException ex) {
                                            Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }

                                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                                try {
                                    DynmapUtil.createMarker(args[2], args[0], weRegion);
                                } catch (NullPointerException e) {

                                }

                                sendDone(cs);
                            }

                        }
//remove
                    } else {
                        if (PluginData.regionsReadable.containsKey(PluginData.projectsAll.get(args[0]).idproject)) {
                            if (PluginData.regionsReadable.get(PluginData.projectsAll.get(args[0]).idproject).contains(args[2])) {

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {

                                        try {
                                            String stat = "DELETE " + Mcproject.getPluginInstance().database + ".regions_data WHERE idregion = '" + PluginData.regions.get(args[2]).idr.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate();
                                            String stat2 = "DELETE " + Mcproject.getPluginInstance().database + ".warps_data WHERE idregion = '" + PluginData.regions.get(args[2]).idr.toString() + "' ;";
                                            Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate();
                                            PluginData.loadRegions();
                                            PluginData.loadWarps();
                                            Mcproject.getPluginInstance().sendReload(pl, "regions");
                                            Mcproject.getPluginInstance().sendReload(pl, "warps");
                                        } catch (SQLException ex) {
                                            Logger.getLogger(ProjectArea.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }

                                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                                try {
                                    DynmapUtil.removeMarker(args[2]);
                                    String n = args[2].toUpperCase() + " (" + args[0].toLowerCase() + ")" + ".marker";
                                    DynmapUtil.deleteWarp(n);
                                    PluginData.loadAllDynmap();
                                    Mcproject.getPluginInstance().sendReload(pl, "map");

                                    sendDel(cs);
                                } catch (NullPointerException e) {
                                }

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

    //TODO in another version
    public void checkRegion() {

    }

    public String serialize(Integer[] intlist) {

        StringBuilder builder = new StringBuilder();

        for (Integer intlist1 : intlist) {
            builder.append(String.valueOf(intlist1)).append(";");
        }

        return builder.toString();

    }

    private void sendNoPermission(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't manage this project");
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
        PluginData.getMessageUtil().sendErrorMessage(player, "For a cuboid area make a valid WorldEdit selection first.");
    }
}
