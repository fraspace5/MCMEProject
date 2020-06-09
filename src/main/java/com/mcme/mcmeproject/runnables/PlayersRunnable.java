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

                PluginData.getMin().keySet().forEach((player) -> {
                    try {
                        OfflinePlayer n = Bukkit.getOfflinePlayer(player);
                        Location loc = n.getPlayer().getLocation();
                        if (PluginData.getMin().get(player)) {

                            PluginData.getRegions().keySet().forEach((region) -> {
                                Region r = PluginData.getRegions().get(region).getRegion();
                                if (r.isInside(loc)) {
                                    UUID idproject = PluginData.getRegions().get(region).getIdproject();

                                    if (PluginData.getTemporaryMinute().containsKey(idproject)) {

                                        Integer l = PluginData.getTemporaryMinute().get(idproject) + 1;
                                        PluginData.getTemporaryMinute().remove(idproject);
                                        PluginData.getTemporaryMinute().put(idproject, l);

                                    } else {
                                        PluginData.getTemporaryMinute().put(idproject, 1);
                                    }

                                    if (PluginData.getTodayStat().containsKey("today")) {
                                        PluginData.getTodayStat().get("today").setMin(PluginData.getTodayStat().get("today").getMin() + 1);
                                        if (!PluginData.getTodayStat().get("today").getPlayers().contains(player)) {
                                            PluginData.getTodayStat().get("today").getPlayers().add(player);

                                            if (!PluginData.getTodayStat().get("today").getProjects().contains(idproject)) {
                                                PluginData.getTodayStat().get("today").getProjects().add(idproject);

                                            }

                                        }
                                    } else {
                                        List<UUID> l = new ArrayList();
                                        List<UUID> pr = new ArrayList();
                                        pr.add(idproject);
                                        l.add(player);
                                        PluginData.getTodayStat().put("today", new ProjectStatistics(0, l, 1, pr));

                                    }

                                    PluginData.getMin().remove(player);
                                    PluginData.getMin().put(player, false);
                                }
                            });

                        }

                    } catch (NullPointerException e) {

                    }
                });

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

                ProjectData d = PluginData.getProjectsAll().get(project);
                if (time.equals(d.getUpdated())) {

                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());

                    message.addSimple(ChatColor.RED + "You have to update this project: " + ChatColor.BLUE + project);

                    message.addClickable(ChatColor.GREEN + "\n" + "Click here to update", "/project progress " + project);

                    message.send(pl);

                } else {

                    cancel();

                }

            }

        }.runTaskTimer(Mcproject.getPluginInstance(), 200L, 800L);

    }

}
