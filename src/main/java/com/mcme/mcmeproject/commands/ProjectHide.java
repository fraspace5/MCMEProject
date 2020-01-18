/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.ProjectStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectHide extends ProjectCommand {

    public ProjectHide(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Hide the project in the project list");
        setUsageDescription(" <ProjectName> : Hide the project in the project list");
    }
    private boolean manager;

    private boolean head;

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            head = false;
            manager = false;
            if (PluginData.getProjectsAll().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    if (PluginData.projectsAll.get(args[0]).status.equals(ProjectStatus.SHOWED)) {

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET status = '" + ProjectStatus.HIDDEN.toString() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";
                                    Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                    PluginData.loadProjects();
                                    sendDone(cs);
                                    
                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectHide.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    } else if (PluginData.projectsAll.get(args[0]).status.equals(ProjectStatus.HIDDEN)) {
                        sendAlreadyVisible(cs);
                    } else {
                        sendFinished(cs);
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
                    if (r2.first()) {
                        if (UUID.fromString(r2.getString("staff_uuid")).equals(pl.getUniqueId())) {
                            head = true;

                        }

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

    private void sendAlreadyVisible(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is already hidden from the project list");
    }

    private void sendFinished(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is marked as finished! You can't show it in the list...");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "The project is hidden");
    }

}
