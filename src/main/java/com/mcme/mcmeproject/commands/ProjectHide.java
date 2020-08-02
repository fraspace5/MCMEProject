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

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (PluginData.getProjectsAll().containsKey(args[0])) {
            if (utils.playerPermission(args[0], cs)) {
                Player pl = (Player) cs;
                switch (PluginData.getProjectsAll().get(args[0]).getStatus()) {
                    case SHOWED:
                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {

                                    Mcproject.getPluginInstance().getUpdateStatus().setString(1, ProjectStatus.HIDDEN.toString());
                                    Mcproject.getPluginInstance().getUpdateStatus().setString(2, "0");
                                    Mcproject.getPluginInstance().getUpdateStatus().setLong(3, System.currentTimeMillis());
                                    Mcproject.getPluginInstance().getUpdateStatus().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                    Mcproject.getPluginInstance().getUpdateStatus().executeUpdate();
                                    PluginData.loadProjects();

                                    bungee.sendReload(pl, "projects");
                                    sendDone(cs);

                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectHide.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());
                        break;
                    case HIDDEN:
                        sendAlreadyInvisible(cs);
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

    private void sendAlreadyInvisible(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is already hidden from the project list");
    }

    private void sendFinished(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is marked as finished! You can't show it in the list...");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "The project is hidden");
    }

}
