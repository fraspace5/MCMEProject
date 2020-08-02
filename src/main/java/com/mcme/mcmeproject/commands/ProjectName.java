/*
 Copyright (C) 2020 MCME (Fraspace5)
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author simonagottardi
 */
public class ProjectName extends ProjectCommand {

    public ProjectName(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Change the name of a project");
        setUsageDescription(" <OldProjectName> <NewProjectName>: Change the projectname of a project.");
    }

    @Override
    protected void execute(CommandSender cs, final String... args) {

        if (PluginData.getProjectsAll().containsKey(args[0])) {
            Player pl = (Player) cs;
            if (utils.playerPermission(args[0], cs)) {
                if (!args[0].equals(args[1])) {

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            try {

                                Mcproject.getPluginInstance().getUpdateInformations().setString(1, "name");
                                Mcproject.getPluginInstance().getUpdateInformations().setString(2, args[1]);
                                Mcproject.getPluginInstance().getUpdateInformations().setString(3, String.valueOf(System.currentTimeMillis()));
                                Mcproject.getPluginInstance().getUpdateInformations().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                Mcproject.getPluginInstance().getUpdateInformations().executeUpdate();

                                PluginData.loadProjects();
                                bungee.sendReload(pl, "projects");

                            } catch (SQLException ex) {
                                Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    sendDone(cs, args[1]);
                } else {
                    sendEqual(cs);
                }
            }

        } else {

            sendNoProject(cs);

        }

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendEqual(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Equal name");
    }

    private void sendDone(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Project name updated with " + name);
    }
}
