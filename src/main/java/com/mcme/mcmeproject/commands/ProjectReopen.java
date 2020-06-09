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
import com.mcme.mcmeproject.util.bungee;
import com.mcme.mcmeproject.util.utils;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectReopen extends ProjectCommand {

    public ProjectReopen(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Reopen a finished project");
        setUsageDescription(" <ProjectName> : Reopen a finished project");
    }

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (PluginData.getProjectsAll().containsKey(args[0])) {
            Player pl = (Player) cs;
            if (utils.playerPermission(args[0], cs)) {
                if (PluginData.getProjectsAll().get(args[0]).getStatus().equals(ProjectStatus.FINISHED)) {

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            try {
                                String stat = "UPDATE mcmeproject_project_data SET status = '" + ProjectStatus.SHOWED.toString() + "', endDate '0' WHERE idproject = '" + PluginData.getProjectsAll().get(args[0]).getIdproject().toString() + "' ;";
                                Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                statm.setQueryTimeout(10);
                                statm.executeUpdate(stat);
                                PluginData.loadProjects();
                                bungee.sendReload(pl, "projects");
                                sendDone(cs, args[0]);
                            } catch (SQLException ex) {
                                Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                } else {
                    sendOpenError(cs);
                }
            }
        } else {

            sendNoProject(cs);

        }

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendOpenError(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is already open");
    }

    private void sendDone(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Project " + name + " has been opened!");
    }

}
