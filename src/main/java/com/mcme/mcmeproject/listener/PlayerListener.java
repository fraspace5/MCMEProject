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
package com.mcme.mcmeproject.listener;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PlayersData;
import com.mcme.mcmeproject.runnables.PlayersRunnable;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.data.ProjectStatistics;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.pluginutil.region.Region;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

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

        for (String region : PluginData.regions.keySet()) {

            Region re = PluginData.regions.get(region).region;

            if (re.isInside(loc)) {

                if (PluginData.getTemporaryBlocks().containsKey(pl.getUniqueId())) {

                    if (PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.containsKey(PluginData.regions.get(region).idproject)) {

                        Integer l = PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.get(PluginData.regions.get(region).idproject) + 1;

                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.remove(PluginData.regions.get(region).idproject);
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.put(PluginData.regions.get(region).idproject, l);
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).lastplayed.remove(PluginData.regions.get(region).idproject);
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).lastplayed.put(PluginData.regions.get(region).idproject, System.currentTimeMillis());

                    } else {
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.put(PluginData.regions.get(region).idproject, 1);

                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).lastplayed.put(PluginData.regions.get(region).idproject, System.currentTimeMillis());
                    }

                } else {

                    HashMap<UUID, Integer> r = new HashMap();
                    HashMap<UUID, Long> s = new HashMap();
                    s.put(PluginData.regions.get(region).idproject, System.currentTimeMillis());
                    r.put(PluginData.regions.get(region).idproject, 1);

                    PluginData.getTemporaryBlocks().put(pl.getUniqueId(), new PlayersData(r, s));

                }
                if (PluginData.getMin().containsKey(uuid)) {

                    PluginData.getMin().remove(uuid);
                    PluginData.getMin().put(uuid, true);

                } else {

                    PluginData.getMin().put(uuid, true);
                }
                if (PluginData.getAllblocks().containsKey(uuid)) {
                    int i = PluginData.getAllblocks().get(uuid) + 1;
                    PluginData.getAllblocks().remove(uuid);
                    PluginData.getAllblocks().put(uuid, i);

                } else {

                    PluginData.getAllblocks().put(uuid, 1);
                }

                if (PluginData.getTodayStat().containsKey("today")) {
                    PluginData.getTodayStat().get("today").blocks = PluginData.getTodayStat().get("today").blocks + 1;

                } else {
                    List<UUID> l = new ArrayList();

                    PluginData.getTodayStat().put("today", new ProjectStatistics(1, l, 0));

                }

            }

        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {

        Player pl = e.getPlayer();
        UUID uuid = pl.getUniqueId();
        Location loc = pl.getLocation();
        for (String region : PluginData.regions.keySet()) {

            Region re = PluginData.regions.get(region).region;

            if (re.isInside(loc)) {

                if (PluginData.getTemporaryBlocks().containsKey(pl.getUniqueId())) {

                    if (PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.containsKey(PluginData.regions.get(region).idproject)) {

                        Integer l = PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.get(PluginData.regions.get(region).idproject) + 1;

                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.remove(PluginData.regions.get(region).idproject);
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.put(PluginData.regions.get(region).idproject, l);
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).lastplayed.remove(PluginData.regions.get(region).idproject);
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).lastplayed.put(PluginData.regions.get(region).idproject, System.currentTimeMillis());
                    } else {
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.put(PluginData.regions.get(region).idproject, 1);

                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).lastplayed.put(PluginData.regions.get(region).idproject, System.currentTimeMillis());
                    }

                } else {

                    HashMap<UUID, Integer> r = new HashMap();
                    r.put(PluginData.regions.get(region).idproject, 1);
                    HashMap<UUID, Long> s = new HashMap();
                    s.put(PluginData.regions.get(region).idproject, System.currentTimeMillis());

                    PluginData.getTemporaryBlocks().put(pl.getUniqueId(), new PlayersData(r, s));

                }
                if (PluginData.getMin().containsKey(uuid)) {

                    PluginData.getMin().remove(uuid);
                    PluginData.getMin().put(uuid, Boolean.TRUE);

                } else {

                    PluginData.getMin().put(uuid, Boolean.TRUE);
                }
                if (PluginData.getAllblocks().containsKey(uuid)) {
                    int i = PluginData.getAllblocks().get(uuid) + 1;
                    PluginData.getAllblocks().remove(uuid);
                    PluginData.getAllblocks().put(uuid, i);

                } else {

                    PluginData.getAllblocks().put(uuid, 1);
                }
                if (PluginData.getTodayStat().containsKey("today")) {
                    PluginData.getTodayStat().get("today").blocks = PluginData.getTodayStat().get("today").blocks + 1;

                } else {
                    List<UUID> l = new ArrayList();

                    PluginData.getTodayStat().put("today", new ProjectStatistics(1, l, 0));

                }

            }

        }

    }

    //for update reminder
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        final Player p = e.getPlayer();

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".news_bool WHERE player_uuid = '" + p.getUniqueId().toString() + "' ;";

                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                    if (PluginData.getPlayernotification()) {
                        if (r.first()) {

                            if (r.getBoolean("bool")) {
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        PluginData.sendNews(e);
                                    }

                                }.runTaskLater(Mcproject.getPluginInstance(), 50L);

                            }

                        } else {

                            String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".news_bool (bool, player_uuid) VALUES (true,'" + p.getUniqueId().toString() + "') ; ";
                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    PluginData.sendNews(e);

                                }

                            }.runTaskLater(Mcproject.getPluginInstance(), 50L);
                        }

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

        new BukkitRunnable() {

            @Override
            public void run() {

                for (String project : PluginData.getToday()) {

                    ProjectData d = PluginData.projectsAll.get(project);

                    if (d.head.equals(p.getUniqueId())) {

                        PlayersRunnable.updatedReminderRunnable(project, p, d.updated);
                    } else if (d.assistants.contains(p.getUniqueId())) {
                        PlayersRunnable.updatedReminderRunnable(project, p, d.updated);
                    }

                }
            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player pl = e.getPlayer();
        Location loc = pl.getLocation();
        for (String project : PluginData.projectsAll.keySet()) {
            if (PluginData.regionsReadable.containsKey(PluginData.projectsAll.get(project).idproject)) {

                for (String region : PluginData.regionsReadable.get(PluginData.projectsAll.get(project).idproject)) {
                    Region r = PluginData.regions.get(region).region;
                    ProjectData p = PluginData.projectsAll.get(project);
                    if (PluginData.informedRegion.containsKey(PluginData.regions.get(region).idr)) {
                        if (r.isInside(loc) && !p.status.equals(ProjectStatus.FINISHED) && !p.status.equals(ProjectStatus.HIDDEN)) {

                            if (!PluginData.informedRegion.get(PluginData.regions.get(region).idr).contains(pl.getUniqueId())) {
                                FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                message.addSimple("Welcome " + pl.getName() + " in the area of : " + ChatColor.RED + p.name.toUpperCase() + " project");
                                if (PluginData.regionsReadable.get(PluginData.projectsAll.get(project).idproject).size() != 1) {
                                    message.addSimple("\n" + ChatColor.GREEN + "The area name is: " + ChatColor.RED + region);
                                }
                                message.send(pl);
                                PluginData.informedRegion.get(PluginData.regions.get(region).idr).add(pl.getUniqueId());

                            }

                        } else if (r.isNear(loc, 10) && PluginData.informedRegion.get(PluginData.regions.get(region).idr).contains(pl.getUniqueId())) {
                            PluginData.informedRegion.get(PluginData.regions.get(region).idr).remove(pl.getUniqueId());

                        }
                    } else {
                        List<UUID> l = new ArrayList();
                        PluginData.informedRegion.put(PluginData.regions.get(region).idr, l);
                    }
                }
            }

        }

    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        for (List<UUID> area : PluginData.informedRegion.values()) {
            if (area.contains(event.getPlayer().getUniqueId())) {
                area.remove(event.getPlayer().getUniqueId());
            }
        }
    }

}
