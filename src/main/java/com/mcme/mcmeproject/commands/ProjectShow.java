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
public class ProjectShow extends ProjectCommand {

    public ProjectShow(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Show the project in the project list");
        setUsageDescription(" <ProjectName> : Show the project in the project list");
    }

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (PluginData.getProjectsAll().containsKey(args[0])) {
           
            Player pl = (Player) cs;
            
            if (utils.playerPermission(args[0], cs)) {

                switch (PluginData.getProjectsAll().get(args[0]).getStatus()) {
                    case HIDDEN:
                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    String stat = "UPDATE mcmeproject_project_data SET status = '" + ProjectStatus.SHOWED.toString() + "' WHERE idproject = '" + PluginData.getProjectsAll().get(args[0]).getIdproject().toString() + "' ;";
                                    Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                    statm.setQueryTimeout(10);
                                    statm.executeUpdate(stat);
                                    PluginData.loadProjects();
                                    bungee.sendReload(pl, "projects");
                                    sendDone(cs);
                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());
                        break;
                    case SHOWED:
                        sendAlreadyVisible(cs);
                        break;
                    default:
                        sendFinished(cs);
                        break;
                }
            }
        } else {

            sendNoProject(cs);

        }

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendAlreadyVisible(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is already visible in the project list");
    }

    private void sendFinished(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is marked as finished! You can't show it in the list...");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "The project is visible");
    }

}
