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
import com.mcmiddleearth.pluginutil.region.CuboidRegion;
import com.mcmiddleearth.pluginutil.region.PrismoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    @Override
    protected void execute(CommandSender cs, String... args) {

        Player pl = (Player) cs;
        Location loc = pl.getLocation();
        if (cs instanceof Player) {

            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    if (args[1].equalsIgnoreCase("add")) {

                        if (!PluginData.getProjectdata().get(args[0]).regions.containsKey(args[2])) {

                            weRegion = FawePlayer.wrap(pl).getSelection();

                            if (!(weRegion instanceof com.sk89q.worldedit.regions.CuboidRegion || weRegion instanceof Polygonal2DRegion)) {
                                sendInvalidSelection(pl);
                                return;
                            } else if (weRegion instanceof Polygonal2DRegion) {

                                PluginData.getProjectdata().get(args[0]).regions.put(args[2], new PrismoidRegion(loc, (com.sk89q.worldedit.regions.Polygonal2DRegion) weRegion));

                                try {
                                    DynmapUtil.createMarker(args[2], args[0], weRegion);
                                } catch (NullPointerException e) {

                                }
                                try {
                                    PluginData.onSave(Mcproject.getPluginInstance().getProjectFolder());
                                } catch (IOException ex) {
                                    Logger.getLogger(ProjectCreate.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                sendDone(cs);
                            } else if (weRegion instanceof com.sk89q.worldedit.regions.CuboidRegion) {

                                PluginData.getProjectdata().get(args[0]).regions.put(args[2], new CuboidRegion(loc, (com.sk89q.worldedit.regions.CuboidRegion) weRegion));

                                try {
                                    DynmapUtil.createMarker(args[2], args[0], weRegion);
                                } catch (NullPointerException e) {

                                }
                                try {
                                    PluginData.onSave(Mcproject.getPluginInstance().getProjectFolder());
                                } catch (IOException ex) {
                                    Logger.getLogger(ProjectCreate.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                sendDone(cs);
                            }

                        } else {

                            sendRegion(cs, args[2], args[0]);

                        }

                    } else {

                        if (PluginData.getProjectdata().get(args[0]).regions.containsKey(args[2])) {

                            PluginData.getProjectdata().get(args[0]).regions.remove(args[2]);
                            PluginData.getProjectdata().get(args[0]).warps.remove(args[2]);
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
     public void checkRegion(){
    
    
    
    
    
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
