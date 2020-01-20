/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.listener;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PlayersData;
import com.mcme.mcmeproject.runnables.PlayersRunnable;
import com.mcme.mcmeproject.data.PluginData;
import static com.mcme.mcmeproject.data.PluginData.projectsAll;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.pluginutil.region.Region;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).lastplayed = System.currentTimeMillis();
                    } else {
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.put(PluginData.regions.get(region).idproject, 1);
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).lastplayed = System.currentTimeMillis();
                    }

                } else {

                    HashMap<UUID, Integer> r = new HashMap();
                    r.put(PluginData.regions.get(region).idproject, 1);

                    PluginData.getTemporaryBlocks().put(pl.getUniqueId(), new PlayersData(r, System.currentTimeMillis()));

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
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).lastplayed = System.currentTimeMillis();
                    } else {
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).r.put(PluginData.regions.get(region).idproject, 1);
                        PluginData.getTemporaryBlocks().get(pl.getUniqueId()).lastplayed = System.currentTimeMillis();
                    }

                } else {

                    HashMap<UUID, Integer> r = new HashMap();
                    r.put(PluginData.regions.get(region).idproject, 1);

                    PluginData.getTemporaryBlocks().put(pl.getUniqueId(), new PlayersData(r, System.currentTimeMillis()));

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
                    if (PluginData.getPlayernotification() == true) {
                        if (r.first()) {

                            if (r.getBoolean("bool")) {

                                PlayersRunnable.playerOnJoin(p);

                            }

                        } else {

                            String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".news_bool SET bool = true WHERE player_uuid = '" + p.getUniqueId().toString() + "' ;";
                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                            PlayersRunnable.playerOnJoin(p);
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

                    try {
                        ProjectData d = PluginData.projectsAll.get(project);

                        if (d.head.equals(p.getUniqueId())) {

                            PlayersRunnable.updatedReminderRunnable(project, p, d.updated);
                        }
                        String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".people_data WHERE idproject =" + projectsAll.get(project).idproject.toString() + " AND staff_uuid = '" + p.getUniqueId().toString() + "' ;";

                        final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                        if (r.first()) {

                            PlayersRunnable.updatedReminderRunnable(project, p, d.updated);
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
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

            for (String region : PluginData.regionsReadable.get(PluginData.projectsAll.get(project))) {
                Region r = PluginData.regions.get(region).region;
                ProjectData p = PluginData.projectsAll.get(project);
                if (r.isInside(loc) && !p.status.equals(ProjectStatus.FINISHED) && !p.status.equals(ProjectStatus.HIDDEN)) {

                    if (!PluginData.informedRegion.get(PluginData.regions.get(region).idr).contains(pl.getUniqueId())) {
                        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                        message.addSimple("Welcome " + pl.getName() + " in the area of : " + ChatColor.RED + p.name.toUpperCase() + " project");
                        if (PluginData.regionsReadable.get(PluginData.projectsAll.get(project)).size() != 1) {
                            message.addSimple("\n" + ChatColor.GREEN + "The area name is: " + ChatColor.RED + region);
                        }
                        message.send(pl);
                        PluginData.informedRegion.get(PluginData.regions.get(region).idr).add(pl.getUniqueId());

                    }

                } else if (r.isNear(loc, 10) && PluginData.informedRegion.get(PluginData.regions.get(region).idr).contains(pl.getUniqueId())) {
                    PluginData.informedRegion.get(PluginData.regions.get(region).idr).remove(pl.getUniqueId());

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
