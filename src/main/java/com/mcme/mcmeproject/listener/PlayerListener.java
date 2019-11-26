/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.listener;

import com.mcme.mcmeproject.runnables.PlayersRunnable;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.pluginutil.region.Region;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Fraspace5
 */
public class PlayerListener implements Listener {

    //for hours of work
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {

        Player pl = e.getPlayer();
        UUID uuid = pl.getUniqueId();
        Location loc = pl.getLocation();
        for (String project : PluginData.getProjectdata().keySet()) {

            for (String region : PluginData.getProjectdata().get(project).regions.keySet()) {

                Region re = PluginData.getProjectdata().get(project).regions.get(region);
                if (re.isInside(loc)) {
                    if (PluginData.getProjectdata().get(project).people.containsKey(pl.getName())) {

                        Double l = PluginData.getProjectdata().get(project).people.get(pl.getName()) + 1.0;
                        PluginData.getProjectdata().get(project).people.remove(pl.getName());
                        PluginData.getProjectdata().get(project).people.put(pl.getName(), l);

                    } else {

                        PluginData.getProjectdata().get(project).people.put(pl.getName(), 1.0);

                    }
                    if (PluginData.getMin().containsKey(uuid)) {

                        PluginData.getMin().remove(uuid);
                        PluginData.getMin().put(uuid, Boolean.TRUE);

                    } else {

                        PluginData.getMin().put(uuid, Boolean.TRUE);
                    }

                }
            }

        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {

        Player pl = e.getPlayer();
        UUID uuid = pl.getUniqueId();
        Location loc = pl.getLocation();
        for (String project : PluginData.getProjectdata().keySet()) {

            for (String region : PluginData.getProjectdata().get(project).regions.keySet()) {

                Region re = PluginData.getProjectdata().get(project).regions.get(region);
                if (re.isInside(loc)) {
                    if (PluginData.getProjectdata().get(project).people.containsKey(pl.getName())) {

                        Double l = PluginData.getProjectdata().get(project).people.get(pl.getName()) + 1.0;
                        PluginData.getProjectdata().get(project).people.remove(pl.getName());
                        PluginData.getProjectdata().get(project).people.put(pl.getName(), l);

                    } else {

                        PluginData.getProjectdata().get(project).people.put(pl.getName(), 1.0);

                    }
                    if (PluginData.getMin().containsKey(uuid)) {

                        PluginData.getMin().remove(uuid);
                        PluginData.getMin().put(uuid, Boolean.TRUE);

                    } else {

                        PluginData.getMin().put(uuid, Boolean.TRUE);
                    }

                }
            }

        }

    }

    //for update reminder
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        if (PluginData.getPlayernotification() == true) {

            if (PluginData.getNews().containsKey(p.getUniqueId())) {

                if (PluginData.getNews().get(p.getUniqueId()).equals(Boolean.TRUE)) {

                    PlayersRunnable.playerOnJoin(p);

                }

            } else {
                PluginData.getNews().put(p.getUniqueId(), Boolean.TRUE);
                PlayersRunnable.playerOnJoin(p);
            }

        }

        for (String project : PluginData.getToday()) {

            ProjectData d = PluginData.getProjectdata().get(project);

            if (d.head.equals(p.getUniqueId())) {

                PlayersRunnable.updatedReminderRunnable(project, p, d.updated);
            }
            for (String manager : d.managers) {
                OfflinePlayer n = Bukkit.getOfflinePlayer(manager);
                if (n.getUniqueId().equals(p.getUniqueId())) {

                    PlayersRunnable.updatedReminderRunnable(project, p, d.updated);
                }

            }

        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player pl = e.getPlayer();
        Location loc = pl.getLocation();
        for (String project : PluginData.getProjectdata().keySet()) {

            for (String region : PluginData.getProjectdata().get(project).regions.keySet()) {
                Region r = PluginData.getProjectdata().get(project).regions.get(region);
                ProjectData p = PluginData.getProjectdata().get(project);
                if (r.isInside(loc) && !p.status.equals(ProjectStatus.FINISHED) && !p.status.equals(ProjectStatus.HIDDEN)) {

                    if (!p.informed.contains(pl.getUniqueId())) {
                        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                        message.addSimple("Welcome " + pl.getName() + " in the area of : " + ChatColor.RED + p.name.toUpperCase() + " project");
                        if (PluginData.getProjectdata().get(project).regions.size() != 1) {
                            message.addSimple("\n" + ChatColor.GREEN + "The area name is: " + ChatColor.RED + region);
                        }
                        message.send(pl);
                        p.informed.add(pl.getUniqueId());
                    }

                } else if (r.isNear(loc, 10) && p.informed.contains(pl.getUniqueId())) {

                    p.informed.remove(pl.getUniqueId());

                }
            }

        }

    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        for (ProjectData area : PluginData.getProjectdata().values()) {
            if (area.informed.contains(event.getPlayer().getUniqueId())) {
                area.informed.remove(event.getPlayer().getUniqueId());
            }
        }
    }

}
