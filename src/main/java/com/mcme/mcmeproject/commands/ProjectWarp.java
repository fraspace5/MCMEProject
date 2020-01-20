/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.DynmapUtil;
import com.mcme.mcmeproject.util.ProjectStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;
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

                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {
                                        if (PluginData.warps.containsKey(PluginData.regions.get(args[1]).idr)) {
                                            String stat2 = "DELETE " + Mcproject.getPluginInstance().database + ".warps_data WHERE idregion = '" + PluginData.regions.get(args[1]).idr.toString() + "' ;";

                                            Mcproject.getPluginInstance().con.prepareStatement(stat2).executeUpdate(stat2);
                                        } else {

                                        }

                                        String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".warps_data (idproject, idregion, world, server, x, y, z ) VALUES ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','" + loc.getWorld().getName() + "','" + Bukkit.getServer().getName() + "','" + loc.getX() + "','" + loc.getY() + "','" + loc.getZ() + "') ;";
                                        Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                        PluginData.loadWarps();
                                        //TODO SERVER LOADING
                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());

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

    public boolean playerPermission(final String prr, CommandSender cs) {
        final Player pl = (Player) cs;
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".staff_data WHERE idproject =" + PluginData.getProjectsAll().get(prr).idproject.toString() + " AND staff_uuid =" + pl.getUniqueId().toString() + " ;";

                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                    if (r.first()) {
                        manager = true;

                    }

                    if (PluginData.projectsAll.get(prr).head.equals(pl.getUniqueId())) {
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
