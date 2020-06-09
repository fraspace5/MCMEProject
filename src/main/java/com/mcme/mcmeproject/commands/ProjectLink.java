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
public class ProjectLink extends ProjectCommand {

    public ProjectLink(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Add a link to a project");
        setUsageDescription(" <ProjectName> <link>: Set the link of the project");
    }

    @Override
    protected void execute(CommandSender cs, final String... args) {

        if (PluginData.getProjectsAll().containsKey(args[0])) {
            Player pl = (Player) cs;
            if (utils.playerPermission(args[0], cs)) {

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        try {
                            String stat = "UPDATE mcmeproject_project_data SET link = '" + args[1] + "', updated = '" + System.currentTimeMillis() + "' WHERE idproject = '" + PluginData.getProjectsAll().get(args[0]).getIdproject().toString() + "' ;";
                            Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                            statm.setQueryTimeout(10);
                            statm.executeUpdate(stat);
                            PluginData.loadProjects();

                            bungee.sendReload(pl, "projects");

                        } catch (SQLException ex) {
                            Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                sendDone(cs);
            }
        } else {

            sendNoProject(cs);

        }

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Project link updated!");
    }
}
