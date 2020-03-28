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
import java.sql.SQLException;
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
public class ProjectLeader extends ProjectCommand {

    public ProjectLeader(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Sets the Head Project");
        setUsageDescription(" <ProjectName> <PlayerName>: Set <PlayerName> as Head project  ");
    }

    private boolean manager;

    private boolean head;

    @Override
    protected void execute(CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            head = false;
            manager = false;
            if (PluginData.projectsAll.containsKey(args[0])) {
                Player pl = (Player) cs;
                if (playerPermission(args[0], cs)) {
                    try {
                        if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {

                            OfflinePlayer n = Bukkit.getOfflinePlayer(args[1]);
                            final UUID uuid = n.getUniqueId();

                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {
                                        String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET staff_uuid = '" + uuid.toString() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";
                                        Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                        PluginData.loadProjects();
                                        Mcproject.getPluginInstance().sendReload(pl, "projects");
                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectLeader.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());

                            sendDone(cs);
                        } else {
                            sendNoPlayer(cs);
                        }
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

        if (PluginData.projectsAll.get(prr).assistants.contains(pl.getUniqueId())) {
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
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendNoPlayer(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid Player");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Head Project updated!");
    }
}
