/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.data.ProjectGotData;
import com.mcme.mcmeproject.util.ProjectStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author fraspace5
 */
public class ProjectMain extends ProjectCommand {

    public ProjectMain(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Set a project as the main project of the server");
        setUsageDescription(" <ProjectName> : Set this project as main");
    }

    public static List<String> mainproject;

    private boolean manager;

    private boolean head;

    @Override
    protected void execute(CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            manager = false;
            head = false;
            if (PluginData.projectsAll.containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {

                    ProjectGotData pr = PluginData.projectsAll.get(args[0]);
                    createList();
                    if (pr.main == true) {
                        sendAlreadyMain(cs);
                    } else {
                        for (String s : mainproject) {
                            final ProjectGotData p = PluginData.projectsAll.get(s);

                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {
                                        String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET main = false WHERE idproject = '" + p.idproject.toString() + "' ;";
                                        Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                        
                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());

                        }

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET main = true WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";
                                    Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                    //TODO SERVER LOADING
                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

                        sendDone(cs, args[0]);
                        PluginData.loadProjects();

                    }
                }
            } else {

                sendNoProject(cs);

            }

        }

    }

    public static void createList() {
        mainproject.clear();
        for (String name : PluginData.projectsAll.keySet()) {

            if (PluginData.projectsAll.get(name).main) {
                mainproject.add(name);
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

    private void sendAlreadyMain(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is already the main project of the server.");
    }

    private void sendDone(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, name + " is the new main project of MCME");
    }
}
