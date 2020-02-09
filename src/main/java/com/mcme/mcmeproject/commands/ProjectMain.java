/*
 *Copyright (C) 2020 MCME (Fraspace5)
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author fraspace5
 */
public class ProjectMain extends ProjectCommand {

    public ProjectMain(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Set a project as the main project of the server");
        setUsageDescription(" <ProjectName> : Set this project as main");
    }

    public static List<String> mainproject = new ArrayList();

    private boolean manager;

    private boolean head;

    @Override
    protected void execute(CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            manager = false;
            head = false;
            if (PluginData.projectsAll.containsKey(args[0])) {
                Player pl = (Player) cs;
                if (playerPermission(args[0], cs)) {

                    ProjectData pr = PluginData.projectsAll.get(args[0]);
                    createList();
                    if (pr.main == true) {
                        sendAlreadyMain(cs);
                    } else {
                        for (String s : mainproject) {
                            final ProjectData p = PluginData.projectsAll.get(s);

                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {
                                        String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET main = 0 WHERE idproject = '" + p.idproject.toString() + "' ;";
                                        Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());

                        }

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET main = 1 WHERE idproject = '" + pr.idproject.toString() + "' ;";
                                    Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                                    sendDone(cs, args[0]);
                                    PluginData.loadProjects();
                                    Mcproject.getPluginInstance().sendReload(pl, "projects");
                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    }
                }
            } else {

                sendNoProject(cs);

            }

        }

    }

    public static void createList() {
        mainproject.clear();
        for (String name : PluginData.projectsAll.keySet()) {

            if (PluginData.projectsAll.get(name).main) {
                mainproject.add(name);
            }

        }

    }

    public boolean playerPermission(final String prr, CommandSender cs) {
        final Player pl = (Player) cs;

        if (PluginData.projectsAll.get(prr).assistants.equals(pl.getUniqueId())) {
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

    private void sendNoPermission(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't manage this project");
    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendAlreadyMain(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is already the main project of the server.");
    }

    private void sendDone(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, name + " is the new main project of MCME");
    }
}
