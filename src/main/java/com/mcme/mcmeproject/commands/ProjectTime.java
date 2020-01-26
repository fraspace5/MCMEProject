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
public class ProjectTime extends ProjectCommand {

    public ProjectTime(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Set the estimated time for the finish of the project");
        setUsageDescription(" <ProjectName> <ExtimatedTime>: Change time");
    }

    private boolean manager;

    private boolean head;

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            head = false;
            manager = false;
            if (PluginData.projectsAll.containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    setTime(args[1], cs, args[0]);

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            try {
                                String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET time = '" + setTime(args[1], cs, args[0]) + "' WHERE idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                                Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);

                                sendDone(cs);
                            } catch (SQLException ex) {
                                Logger.getLogger(ProjectFinish.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                }
            } else {

                sendNoProject(cs);

            }

        }

    }

    public Long setTime(String t, CommandSender cs, String nameProject) {
        String tt = t.substring(0, t.length() - 1);
        if (t.endsWith("y")) {
            Double s = 86400000 * 365 * parseDouble(tt);
            Long r = s.longValue() + System.currentTimeMillis();
            return r;

            //years 365 days
        } else if (t.endsWith("m")) {
            Double s = 86400000 * (31 * parseDouble(tt));
            Long r = s.longValue() + System.currentTimeMillis();

            return r;

//month 31 days
        } else if (t.endsWith("w")) {
            Double s = 86400000 * (7 * parseDouble(tt));
            Long r = s.longValue() + System.currentTimeMillis();
            return r;
//week 7 days
        } else if (t.endsWith("d")) {
            Double s = 86400000 * parseDouble(tt);
            Long r = s.longValue() + System.currentTimeMillis();
            return r;

//days
        } else {

            sendNoTime(cs);

            return null;
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
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Time updated!");
    }

    private void sendNoTime(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Error with the time value!");
    }
}
