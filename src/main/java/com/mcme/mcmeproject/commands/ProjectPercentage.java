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
import static java.lang.Double.parseDouble;
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
public class ProjectPercentage extends ProjectCommand {

    public ProjectPercentage(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Sets the percentage of the project.");
        setUsageDescription(" <ProjectName> <Percentage>: Set the percentage value of a project.");
    }

    @Override
    protected void execute(CommandSender cs, final String... args) {

        if (PluginData.getProjectsAll().containsKey(args[0])) {
            Player pl = (Player) cs;
            if (utils.playerPermission(args[0], cs)) {
                if (args[1].endsWith("%")) {
                    
                    sendPercentage(args, pl, args[1].substring(0, args[1].length() - 1), cs);

                } else {

                    try {
                        if (parseDouble(args[1]) > 100.0) {

                            sendNoPercentage(cs);

                        } else {

                            sendPercentage(args, pl, args[1], cs);

                        }
                    } catch (NumberFormatException e) {
                        PluginData.getMessageUtil().sendErrorMessage(cs, "It should be a number or it should end with % ");

                    }
                }

            }
        } else {

            sendNoProject(cs);

        }

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendNoPercentage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Percentage value should be less than 100 ");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Percentage updated!");
    }

    private void sendPercentage(String[] args, Player pl, String percentage, CommandSender cs) {

        new BukkitRunnable() {

            @Override
            public void run() {

                try {
                    Mcproject.getPluginInstance().getUpdateInformations().setString(1, "percentage");
                    Mcproject.getPluginInstance().getUpdateInformations().setString(2, percentage);
                    Mcproject.getPluginInstance().getUpdateInformations().setLong(3, System.currentTimeMillis());
                    Mcproject.getPluginInstance().getUpdateInformations().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                    Mcproject.getPluginInstance().getUpdateInformations().executeUpdate();

                    PluginData.loadProjects();
                    bungee.sendReload(pl, "projects");
                } catch (SQLException ex) {
                    Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

        sendDone(cs);
    }

}
