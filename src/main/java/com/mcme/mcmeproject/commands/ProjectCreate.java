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
public class ProjectCreate extends ProjectCommand {

    public ProjectCreate(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Create a new project");
        setUsageDescription(" <ProjectName>: Create a new project");
    }

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            final Player pl = (Player) cs;
            if (!PluginData.getProjectsAll().containsKey(args[0])) {

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        try {

                            String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data (idproject, name, staff_uuid, startDate, percentage, link, time, description, updated, status, main, jobs, minutes, endDate, assistants, plcurrent) VALUES ('" + PluginData.createId().toString() + "', '" + args[0] + "', '" + pl.getUniqueId().toString() + "', '" + System.currentTimeMillis() + "', '0', 'nothing', '" + System.currentTimeMillis() + "', ' ', '" + System.currentTimeMillis() + "', '" + ProjectStatus.HIDDEN.name().toUpperCase() + "', 0, ' ', '0', '0', ' ', ' ') ;";
                            Mcproject.getPluginInstance().con.prepareStatement(stat).execute();
                            
                            sendCreated(cs, args[0]);
                            PluginData.loadProjects();
                            Mcproject.getPluginInstance().sendReload(pl, "projects");

                        } catch (SQLException ex) {
                            Logger.getLogger(ProjectCreate.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

            } else {

                sendAlreadyProject(cs);

            }

        }

    }

    private void sendAlreadyProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project already exists");
    }

    private void sendCreated(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "New project " + name + " created! Add new information, type /project help");
    }

}
