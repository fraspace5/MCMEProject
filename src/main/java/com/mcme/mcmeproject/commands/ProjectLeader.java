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

    @Override
    protected void execute(CommandSender cs, final String... args) {

        if (PluginData.getProjectsAll().containsKey(args[0])) {
            Player pl = (Player) cs;
            if (utils.playerPermission(args[0], cs)) {

                if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {

                    OfflinePlayer n = Bukkit.getOfflinePlayer(args[1]);
                    final UUID uuid = n.getUniqueId();
                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            try {
                                Mcproject.getPluginInstance().getUpdateInformations().setString(1, "staff_uuid");
                                Mcproject.getPluginInstance().getUpdateInformations().setString(2, uuid.toString());
                                Mcproject.getPluginInstance().getUpdateInformations().setLong(3, System.currentTimeMillis());
                                Mcproject.getPluginInstance().getUpdateInformations().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                Mcproject.getPluginInstance().getUpdateInformations().executeUpdate();
                                PluginData.loadProjects();
                                bungee.sendReload(pl, "projects");

                            } catch (SQLException ex) {
                                Logger.getLogger(ProjectLeader.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    sendDone(cs);
                } else {
                    sendNoPlayer(cs);
                }

            }
        } else {

            sendNoProject(cs);

        }

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
