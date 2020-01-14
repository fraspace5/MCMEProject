/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.runnables;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
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
                    if (PluginData.getMin().get(player).booleanValue() == true) {

                        for (String project : PluginData.getProjectdata().keySet()) {

                            for (String region : PluginData.getProjectdata().get(project).regions.keySet()) {

                                Region r = PluginData.getProjectdata().get(project).regions.get(region);

                                if (r.isInside(loc)) {

                                    PluginData.getProjectdata().get(project).minutes = PluginData.getProjectdata().get(project).minutes + 1;
                                    PluginData.getMin().remove(player);
                                    PluginData.getMin().put(player, Boolean.FALSE);
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

        }.runTaskTimer(Mcproject.getPluginInstance(), 60L, 288000L);

    }

    public static void playerOnJoin(final Player pl) {

        new BukkitRunnable() {

            @Override
            public void run() {

                PluginData.sendNews(pl);
            }

        }.runTaskLater(Mcproject.getPluginInstance(), 100L);

    }

    public static void updatedReminderRunnable(final String project, final Player pl, final Long time) {

        new BukkitRunnable() {

            @Override
            public void run() {

                ProjectData d = PluginData.getProjectdata().get(project);
                if (time.equals(d.updated)) {

                    Player p = pl;

                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());

                    message.addSimple(ChatColor.RED + "You have to update this project: " + ChatColor.BLUE + project);

                    message.addClickable(ChatColor.GREEN + "\n" + "Click here to update", "/project progress percentage time");

                    message.send(p);

                } else {

                    cancel();

                }

            }

        }.runTaskTimer(Mcproject.getPluginInstance(), 200L, 800L);

    }

    public static void restorePeople() {
        new BukkitRunnable() {

            @Override
            public void run() {

                Long r = PluginData.getT() + 86400;

                if (r < System.currentTimeMillis()) {

                    for (String n : PluginData.getProjectdata().keySet()) {

                        PluginData.getProjectdata().get(n).people.clear();
                        PluginData.setT(System.currentTimeMillis());
                    }
                }
            }

        }
                .runTaskTimer(Mcproject.getPluginInstance(), 1200L, 36000L);
    }

}
