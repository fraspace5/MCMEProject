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
import com.mcme.mcmeproject.util.bungee;
import com.mcme.mcmeproject.util.utils;
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
 * @author Fraspace5
 */
public class ProjectMain extends ProjectCommand {

    public ProjectMain(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Set a project as the main project of the server");
        setUsageDescription(" <ProjectName> true|false : Set this project as main");
    }

    private List<String> mainproject = new ArrayList();

    @Override
    protected void execute(CommandSender cs, final String... args) {

        if (PluginData.getProjectsAll().containsKey(args[0])) {

            Player pl = (Player) cs;
            if (utils.playerPermission(args[0], cs)) {

                ProjectData pr = PluginData.getProjectsAll().get(args[0]);
                createList();

                if (args[1].equalsIgnoreCase("true")) {
                    if (pr.isMain()) {
                        sendAlreadyMain(cs);
                    } else {
                        if (mainproject.size() != 2) {
                            new BukkitRunnable() {

                                @Override
                                public void run() {

                                    try {

                                        Mcproject.getPluginInstance().getUpdateInformations().setString(1, "main");
                                        Mcproject.getPluginInstance().getUpdateInformations().setString(2, "1");
                                        Mcproject.getPluginInstance().getUpdateInformations().setLong(3, System.currentTimeMillis());
                                        Mcproject.getPluginInstance().getUpdateInformations().setString(4, pr.getIdproject().toString());
                                        Mcproject.getPluginInstance().getUpdateInformations().executeUpdate();

                                        sendDone(cs, args[0]);
                                        PluginData.loadProjects();
                                        bungee.sendReload(pl, "projects");
                                    } catch (SQLException ex) {
                                        Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            }.runTaskAsynchronously(Mcproject.getPluginInstance());
                        } else {
                            sendTooMuch(cs);
                        }
                    }
                } else if (args[1].equalsIgnoreCase("false")) {
                    if (!pr.isMain()) {
                        sendAlreadyFalse(cs);
                    } else {
                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {
                                    Mcproject.getPluginInstance().getUpdateInformations().setString(1, "main");
                                    Mcproject.getPluginInstance().getUpdateInformations().setString(2, "0");
                                    Mcproject.getPluginInstance().getUpdateInformations().setLong(3, System.currentTimeMillis());
                                    Mcproject.getPluginInstance().getUpdateInformations().setString(4, pr.getIdproject().toString());
                                    Mcproject.getPluginInstance().getUpdateInformations().executeUpdate();
                                    
                                    sendDoneOff(cs, args[0]);
                                    PluginData.loadProjects();
                                    bungee.sendReload(pl, "projects");
                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());
                    }
                } else {
                    sendError(cs);
                }

            }
        } else {

            sendNoProject(cs);

        }

    }

    private void createList() {
        mainproject.clear();
        PluginData.getProjectsAll().keySet().forEach((name) -> {
            if (PluginData.getProjectsAll().get(name).isMain()) {
                mainproject.add(name);
            }
        });

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendAlreadyFalse(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project isn't a main project");
    }

    private void sendError(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid Usage, use true|false");
    }

    private void sendTooMuch(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "There are too much main projects");
    }

    private void sendAlreadyMain(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is already the main project of the server.");
    }

    private void sendDone(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, name + " is the new main project of MCME");
    }

    private void sendDoneOff(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, name + " isn't a main project anymore");
    }
}
