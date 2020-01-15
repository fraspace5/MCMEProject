/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.boydti.fawe.object.FawePlayer;
import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.DynmapUtil;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.region.CuboidRegion;
import com.mcmiddleearth.pluginutil.region.PrismoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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

                        if (!PluginData.regionsReadable.get(PluginData.projectsAll.get(args[1]).idproject).contains(args[2])) {

                            weRegion = FawePlayer.wrap(pl).getSelection();

                            if (!(weRegion instanceof com.sk89q.worldedit.regions.CuboidRegion || weRegion instanceof Polygonal2DRegion)) {
                                sendInvalidSelection(pl);
                                return;
                            } else if (weRegion instanceof Polygonal2DRegion) {

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {

                                        try {
                                            PrismoidRegion r = new PrismoidRegion(loc, (com.sk89q.worldedit.regions.Polygonal2DRegion) weRegion);

                                            String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".regions_data (idproject, idregion, name, type, xlist, zlist, ymin, ymax, location ) VALUES ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','" + PluginData.createId().toString() + "','" + args[2] + "','prismoid','" + serialize(r.getXPoints()) + "','" + serialize(r.getZPoints()) + "','" + r.getMinY() + "','" + r.getMaxY() + "','" + pl.getLocation().getWorld().getName() + ";" + pl.getLocation().getX() + ";" + pl.getLocation().getY() + ";" + pl.getLocation().getZ() + "') ;";
                                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                            PluginData.loadProjects();
                                            PluginData.loadWarps();
                                            //TODO SERVER LOADING
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

                                            String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".regions_data (idproject, idregion, name, type, xlist, zlist, ymin, ymax, location ) VALUES ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','" + PluginData.createId().toString() + "','" + args[2] + "','cuboid','" + minCorner.getBlockX() + ";" + maxCorner.getBlockX() + "','" + minCorner.getBlockZ() + ";" + maxCorner.getBlockZ() + "','" + minCorner.getBlockY() + "','" + maxCorner.getBlockY() + "','" + pl.getLocation().getWorld().getName() + ";" + pl.getLocation().getX() + ";" + pl.getLocation().getY() + ";" + pl.getLocation().getZ() + "') ;";
                                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                            PluginData.loadRegions();
                                            PluginData.loadWarps();
                                            //TODO SERVER LOADING
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
//remove
                    } else {

                        if (PluginData.regionsReadable.get(PluginData.projectsAll.get(args[1]).idproject).contains(args[2])) {

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

                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectArea.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());

                            try {
                                DynmapUtil.removeMarker(args[2]);
                                String n = args[2].toUpperCase() + " (" + args[0].toLowerCase() + ")" + ".marker";
                                DynmapUtil.deleteWarp(n);
                                sendDel(cs);
                            } catch (NullPointerException e) {
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
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".staff_data WHERE idproject =" + PluginData.getProjectsAll().get(prr).idproject.toString() + " AND staff_uuid =" + pl.getUniqueId().toString() + " ;";

                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                    String st = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".project_data WHERE idproject =" + PluginData.getProjectsAll().get(prr).idproject.toString() + " ;";

                    final ResultSet r2 = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                    if (r.first()) {
                        manager = true;

                    }
                    if (UUID.fromString(r2.getString("staff_uuid")).equals(pl.getUniqueId())) {
                        head = true;

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ProjectAdd.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

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

        for (int i = 0; i < intlist.length; i++) {

            builder.append(String.valueOf(intlist[i]) + ";");

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
