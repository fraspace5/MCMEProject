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
public class ProjectRemove extends ProjectCommand {

    public ProjectRemove(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Remove a manager from the list");
        setUsageDescription(" <ProjectName> <PlayerName>: Remove a manager from a project");
    }
    private boolean manager;

    private boolean head;

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            manager = false;
            head = false;
            Player pl = (Player) cs;
            if (PluginData.projectsAll.containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            try {
                                OfflinePlayer n = Bukkit.getOfflinePlayer(args[1]);
                                UUID uuid = n.getUniqueId();
                                ProjectData p = PluginData.getProjectsAll().get(args[0]);

                                if (!p.assistants.contains(uuid)) {

                                    sendManagerError(cs, args[1], args[0]);

                                } else {

                                    final List<UUID> assist = p.assistants;

                                    assist.remove(uuid);
                                    String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET assistants = '" + serialize(assist) + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";
                                    Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate();
                                    sendManager(cs, args[1]);
                                    PluginData.loadProjects();
                                    Mcproject.getPluginInstance().sendReload(pl, "projects");

                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(ProjectAdd.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                }
            } else {

                sendNoProject(cs);

            }

        }

    }

    public boolean playerPermission(final String prr, CommandSender cs) {
        final Player pl = (Player) cs;

        if (PluginData.projectsAll.get(prr).assistants.contains(pl.getUniqueId())) {
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

    public String serialize(List<UUID> intlist) {

        StringBuilder builder = new StringBuilder();
        if (!intlist.isEmpty()) {
            for (UUID s : intlist) {

                builder.append(s.toString() + ";");

            }
        }
        return builder.toString();

    }

    private void sendNoPermission(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't manage this project");
    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendManagerError(CommandSender cs, String n, String p) {
        PluginData.getMessageUtil().sendErrorMessage(cs, n + " is not a manager of " + p);
    }

    private void sendManager(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Manager " + name + " removed!");
    }

}
