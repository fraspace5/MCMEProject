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
import com.mcme.mcmeproject.data.RegionData;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcme.mcmeproject.util.bungee;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.pluginutil.region.Region;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {

        elaborateEvent(e);

    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        elaborateEvent(e);

    }

    //for update reminder
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        final Player p = e.getPlayer();
        new BukkitRunnable() {

            @Override
            public void run() {
                if (Mcproject.getPluginInstance().getNameserver().equals("default")) {

                    bungee.sendNameServer(p);
                    PluginData.loadAllDynmap();

                }
            }

        }.runTaskLater(Mcproject.getPluginInstance(), 150L);

        System.out.println("Project " + Mcproject.getPluginInstance().getNameserver());

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    String statement = "SELECT * FROM mcmeproject_news_bool WHERE player_uuid = '" + p.getUniqueId().toString() + "' ;";

                    Statement statm1 = Mcproject.getPluginInstance().getConnection().prepareStatement(statement);
                    statm1.setQueryTimeout(10);
                    final ResultSet r = statm1.executeQuery(statement);

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

                            String stat = "INSERT INTO mcmeproject_news_bool (bool, player_uuid) VALUES (true,'" + p.getUniqueId().toString() + "') ; ";
                            Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                            statm.setQueryTimeout(10);
                            statm.executeUpdate(stat);
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

                PluginData.getToday().forEach((project) -> {
                    ProjectData d = PluginData.getProjectsAll().get(project);

                    if (d.getHead().equals(p.getUniqueId())) {

                        PlayersRunnable.updatedReminderRunnable(project, p, d.getUpdated());
                    } else if (d.getAssistants().contains(p.getUniqueId())) {
                        PlayersRunnable.updatedReminderRunnable(project, p, d.getUpdated());
                    }
                });
            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player pl = e.getPlayer();
        Location loc = pl.getLocation();
        PluginData.getProjectsAll().keySet().forEach((project) -> {
            if (PluginData.getRegionsReadable().containsKey(PluginData.getProjectsAll().get(project).getIdproject())) {

                PluginData.getRegionsReadable().get(PluginData.getProjectsAll().get(project).getIdproject()).forEach((region) -> {
                    Region r = PluginData.getRegions().get(region).getRegion();
                    ProjectData p = PluginData.getProjectsAll().get(project);
                    if (PluginData.getInformedRegion().containsKey(PluginData.getRegions().get(region).getIdr())) {
                        if (r.isInside(loc) && !p.getStatus().equals(ProjectStatus.FINISHED) && !p.getStatus().equals(ProjectStatus.HIDDEN)) {

                            if (!PluginData.getInformedRegion().get(PluginData.getRegions().get(region).getIdr()).contains(pl.getUniqueId())) {
                                FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                message.addSimple("Welcome " + pl.getName() + " in the area of : " + ChatColor.RED + p.getName().toUpperCase() + " project");
                                if (PluginData.getRegionsReadable().get(PluginData.getProjectsAll().get(project).getIdproject()).size() != 1) {
                                    message.addSimple("\n" + ChatColor.GREEN + "The area name is: " + ChatColor.RED + region);
                                }
                                message.send(pl);
                                PluginData.getInformedRegion().get(PluginData.getRegions().get(region).getIdr()).add(pl.getUniqueId());

                            }

                        } else if (!r.isNear(loc, 100) && PluginData.getInformedRegion().get(PluginData.getRegions().get(region).getIdr()).contains(pl.getUniqueId())) {
                            PluginData.getInformedRegion().get(PluginData.getRegions().get(region).getIdr()).remove(pl.getUniqueId());
                        }
                    } else {
                        List<UUID> l = new ArrayList();
                        PluginData.getInformedRegion().put(PluginData.getRegions().get(region).getIdr(), l);
                    }
                });
            }
        });

    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        PluginData.getInformedRegion().values().forEach((area) -> {
            if (area.contains(event.getPlayer().getUniqueId())) {
                area.remove(event.getPlayer().getUniqueId());
            }
        });
    }

    private void elaborateEvent(Event e) {

        Player pl = null;
        if (e instanceof BlockBreakEvent) {
            pl = ((BlockBreakEvent) e).getPlayer();
        } else if (e instanceof BlockPlaceEvent) {
            pl = ((BlockPlaceEvent) e).getPlayer();
        }
     
        if (pl != null) {
            UUID uuid = pl.getUniqueId();
            Location loc = pl.getLocation();
            List<String> reg = new ArrayList<>();
            PluginData.getRegions().keySet().forEach((region) -> {
                Region re = PluginData.getRegions().get(region).getRegion();
                if (re.isInside(loc)) {
                    reg.add(region);
                }
            });

            if (!reg.isEmpty()) {
                String weightMax = reg.get(0);

                for (String re : reg) {
                    if (PluginData.getRegions().get(re).getWeight() > PluginData.getRegions().get(weightMax).getWeight()) {
                        weightMax = re;
                    }
                }
                RegionData region = PluginData.getRegions().get(weightMax);

                if (region.getRegion().isInside(loc)) {

                    if (PluginData.getTemporaryBlocks().containsKey(pl.getUniqueId())) {

                        if (PluginData.getTemporaryBlocks().get(pl.getUniqueId()).getR().containsKey(region.getIdproject())) {

                            Integer l = PluginData.getTemporaryBlocks().get(pl.getUniqueId()).getR().get(region.getIdproject()) + 1;

                            PluginData.getTemporaryBlocks().get(pl.getUniqueId()).getR().remove(region.getIdproject());
                            PluginData.getTemporaryBlocks().get(pl.getUniqueId()).getR().put(region.getIdproject(), l);
                            PluginData.getTemporaryBlocks().get(pl.getUniqueId()).getLastplayed().remove(region.getIdproject());
                            PluginData.getTemporaryBlocks().get(pl.getUniqueId()).getLastplayed().put(region.getIdproject(), System.currentTimeMillis());

                        } else {
                            PluginData.getTemporaryBlocks().get(pl.getUniqueId()).getR().put(region.getIdproject(), 1);

                            PluginData.getTemporaryBlocks().get(pl.getUniqueId()).getLastplayed().put(region.getIdproject(), System.currentTimeMillis());
                        }

                    } else {

                        HashMap<UUID, Integer> r = new HashMap();
                        HashMap<UUID, Long> s = new HashMap();
                        s.put(region.getIdproject(), System.currentTimeMillis());
                        r.put(region.getIdproject(), 1);

                        PluginData.getTemporaryBlocks().put(pl.getUniqueId(), new PlayersData(r, s));

                    }
                    if (PluginData.getMin().containsKey(uuid)) {

                        PluginData.getMin().remove(uuid);
                        PluginData.getMin().put(uuid, true);

                    } else {

                        PluginData.getMin().put(uuid, true);
                    }
                    if (PluginData.getAllblocks().containsKey(region.getIdproject())) {
                        int i = PluginData.getAllblocks().get(region.getIdproject()) + 1;
                        PluginData.getAllblocks().remove(region.getIdproject());
                        PluginData.getAllblocks().put(region.getIdproject(), i);

                    } else {

                        PluginData.getAllblocks().put(region.getIdproject(), 1);
                    }

                    if (PluginData.getTodayStat().containsKey("today")) {

                        PluginData.getTodayStat().get("today").setBlocks(PluginData.getTodayStat().get("today").getBlocks() + 1);

                        if (!PluginData.getTodayStat().get("today").getProjects().contains(region.getIdproject())) {
                            PluginData.getTodayStat().get("today").getProjects().add(region.getIdproject());

                        }
                    } else {
                        List<UUID> l = new ArrayList();
                        List<UUID> pr = new ArrayList();
                        pr.add(region.getIdproject());
                        PluginData.getTodayStat().put("today", new ProjectStatistics(1, l, 0, pr));

                    }

                }
            }

        }
    }

}
