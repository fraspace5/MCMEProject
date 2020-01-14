/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
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
 * @author Fraspace5
 */
public class ProjectAdd extends ProjectCommand {

    public ProjectAdd(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Add a manager to the list");
        setUsageDescription(" <ProjectName> <PlayerName>: Add a manager to the project");
    }

    private boolean manager;

    private boolean head;

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        head = false;
        manager = false;
        if (cs instanceof Player) {
            final Player pl = (Player) cs;
            if (PluginData.getProjectsAll().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    try {
                        OfflinePlayer n = Bukkit.getOfflinePlayer(args[1]);

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".staff_data WHERE idproject =" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + " AND staff_uuid =" + pl.getUniqueId().toString() + " ;";

                                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                                    if (r.first()) {

                                        sendManagerError(cs);

                                    } else {

                                        if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {

                                            String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".staff_data (staff_uuid, idproject) VALUES ('" + pl.getUniqueId().toString() + "','" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "') ;";
                                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate();
                                            sendManager(cs, args[1]);
                                        }

                                    }
                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectAdd.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    } catch (NullPointerException e) {

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

    private void sendNoPermission(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't manage this project");
    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendManagerError(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This player is already a manager of this project");
    }

    private void sendManager(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Manager " + name + " added!");
    }

}
