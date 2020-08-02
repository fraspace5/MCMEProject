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
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.bungee;
import com.mcme.mcmeproject.util.utils;
import java.sql.SQLException;
import java.util.List;
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
public class ProjectAdd extends ProjectCommand {

    public ProjectAdd(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Add a manager to the list");
        setUsageDescription(" <ProjectName> <PlayerName>: Add a manager to the project");
    }

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        final Player pl = (Player) cs;

        if (PluginData.getProjectsAll().containsKey(args[0])) {
            if (utils.playerPermission(args[0], cs)) {
                try {
                    OfflinePlayer n = Bukkit.getOfflinePlayer(args[1]);

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            try {

                                ProjectData p = PluginData.getProjectsAll().get(args[0]);

                                if (p.getAssistants().contains(n.getUniqueId())) {

                                    sendManagerError(cs);

                                } else {

                                    if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                                        final List<UUID> assist = p.getAssistants();
                                        assist.add(n.getUniqueId());
                                        String s = serialize(assist);

                                        Mcproject.getPluginInstance().getUpdateInformations().setString(1, "assistants");
                                        Mcproject.getPluginInstance().getUpdateInformations().setString(2, s);
                                        Mcproject.getPluginInstance().getUpdateInformations().setLong(3, System.currentTimeMillis());
                                        Mcproject.getPluginInstance().getUpdateInformations().setString(4, PluginData.getProjectsAll().get(args[0]).getIdproject().toString());
                                        Mcproject.getPluginInstance().getUpdateInformations().executeUpdate();
                                        PluginData.loadProjects();

                                        bungee.sendReload(pl, "projects");
                                        sendManager(cs, args[1]);
                                    }

                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(ProjectAdd.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                } catch (NullPointerException e) {

                }
            }

        } else {

            sendNoProject(cs);

        }

    }

    private String serialize(List<UUID> intlist) {

        StringBuilder builder = new StringBuilder();
        if (!intlist.isEmpty()) {
            intlist.forEach((s) -> {
                builder.append(s.toString()).append(";");
            });
        }
        return builder.toString();

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendManagerError(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This player is already a manager of this project");
    }

    private void sendManager(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Manager " + name + " added!");
    }

}
