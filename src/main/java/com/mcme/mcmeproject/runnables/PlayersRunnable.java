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
package com.mcme.mcmeproject.runnables;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.data.ProjectStatistics;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.pluginutil.region.Region;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class PlayersRunnable {

    public static void AddMinuteRunnable() {

        new BukkitRunnable() {

            @Override
            public void run() {

                for (UUID player : PluginData.getMin().keySet()) {

                    OfflinePlayer n = Bukkit.getOfflinePlayer(player);
                    Location loc = n.getPlayer().getLocation();
                    if (PluginData.getMin().get(player)) {

                        for (String region : PluginData.regions.keySet()) {

                            Region r = PluginData.regions.get(region).region;

                            if (r.isInside(loc)) {

                                if (PluginData.getTemporaryMinute().containsKey(PluginData.regions.get(region).idproject)) {

                                    Integer l = PluginData.getTemporaryMinute().get(PluginData.regions.get(region).idproject) + 1;
                                    PluginData.getTemporaryMinute().remove(PluginData.regions.get(region).idproject);
                                    PluginData.getTemporaryMinute().put(PluginData.regions.get(region).idproject, l);

                                } else {
                                    PluginData.getTemporaryMinute().put(PluginData.regions.get(region).idproject, 1);
                                }
                                PluginData.getMin().remove(player);
                                PluginData.getMin().put(player, false);
                                if (PluginData.getTodayStat().containsKey("today")) {
                                    PluginData.getTodayStat().get("today").min = PluginData.getTodayStat().get("today").min + 1;
                                    if (!PluginData.getTodayStat().get("today").players.contains(player)) {
                                        PluginData.getTodayStat().get("today").players.add(player);

                                        if (!PluginData.getTodayStat().get("today").projects.contains(PluginData.regions.get(region).idproject)) {
                                            PluginData.getTodayStat().get("today").projects.add(PluginData.regions.get(region).idproject);

                                        }

                                    }
                                } else {
                                    List<UUID> l = new ArrayList();
                                    List<UUID> pr = new ArrayList();
                                    pr.add(PluginData.regions.get(region).idproject);
                                    l.add(player);
                                    PluginData.getTodayStat().put("today", new ProjectStatistics(0, l, 1, pr));

                                }
                            }

                        }

                    }

                }

            }

        }.runTaskTimer(Mcproject.getPluginInstance(), 60L, 1200L);

    }

    public static void SetTodayUpdatedRunnable() {

        new BukkitRunnable() {

            @Override
            public void run() {

                PluginData.setTodayEnd();
            }

        }.runTaskTimer(Mcproject.getPluginInstance(), 400L, 288000L);

    }

    public static void updatedReminderRunnable(final String project, final Player pl, final Long time) {

        new BukkitRunnable() {

            @Override
            public void run() {

                ProjectData d = PluginData.projectsAll.get(project);
                if (time.equals(d.updated)) {

                    Player p = pl;

                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());

                    message.addSimple(ChatColor.RED + "You have to update this project: " + ChatColor.BLUE + project);

                    message.addClickable(ChatColor.GREEN + "\n" + "Click here to update", "/project progress " + project);

                    message.send(p);

                } else {

                    cancel();

                }

            }

        }.runTaskTimer(Mcproject.getPluginInstance(), 200L, 800L);

    }

}
