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
import com.mcme.mcmeproject.util.ProjectStatus;
import java.sql.SQLException;
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
                    Player pl = (Player) cs;
                    if (PluginData.projectsAll.get(args[0]).status.equals(ProjectStatus.SHOWED)) {

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET status = '" + ProjectStatus.HIDDEN.toString() + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";
                                    Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                    PluginData.loadProjects();
                                    Mcproject.getPluginInstance().sendReload(pl, "projects");
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
