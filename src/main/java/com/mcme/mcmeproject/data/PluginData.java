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
package com.mcme.mcmeproject.data;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.util.DynmapUtil;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcme.mcmeproject.util.utils;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import lombok.Getter;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import static java.lang.Integer.parseInt;
import static java.lang.Double.parseDouble;
import com.mcmiddleearth.pluginutil.region.CuboidRegion;
import org.bukkit.Location;
import com.mcmiddleearth.pluginutil.region.PrismoidRegion;
import java.util.Map.Entry;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Fraspace5
 */
public class PluginData {

    @Getter
    private static final MessageUtil messageUtil = new MessageUtil();

    static {
        messageUtil.setPluginName("MC-Project");
    }

    @Getter
    private static Map<String, ProjectData> projectsAll = new HashMap<>();
    @Getter
    private static Map<UUID, String> projectsUUID = new HashMap<>();

    @Getter
    private static Map<String, RegionData> regions = new HashMap<>();
    //name region

    @Getter
    private static Map<UUID, String> regionsUUID = new HashMap<>();
    @Getter
    @Setter
    private static Map<UUID, List<String>> regionsReadable = new HashMap<>();

    @Getter
    private static Map<UUID, WarpData> warps = new HashMap<>();

    @Getter
    private static Map<UUID, List<UUID>> informedRegion = new HashMap<>();
    //regionid, List of playerid
    @Getter
    private static Long time = Mcproject.getPluginInstance().getConfig().getLong("time");

    @Getter
    private static Boolean playernotification = Mcproject.getPluginInstance().getConfig().getBoolean("playernotification");

    @Getter
    private static Map<UUID, Boolean> min = new HashMap<>();
    @Setter
    @Getter
    private static Map<UUID, Integer> temporaryMinute = new HashMap<>();
    //Player id ,List
    @Setter
    @Getter
    private static Map<UUID, PlayersData> temporaryBlocks = new HashMap<>();
    @Setter
    @Getter
    private static Map<UUID, Integer> allblocks = new HashMap<>();
    @Setter
    @Getter
    private static HashMap<String, ProjectStatistics> todayStat = new HashMap<>();
//projectid, number
    @Setter
    @Getter
    private static Long t;

    @Getter
    private static List<String> today = new ArrayList();

    public static void setTodayEnd() {
        today.clear();

        new BukkitRunnable() {

            @Override
            public void run() {
                try {

                    final ResultSet r = Mcproject.getPluginInstance().getSelectProjects().executeQuery();

                    if (r.first()) {
                        do {
                            Integer milliweeks = 1000 * 60 * 60 * 24 * 7;

                            Long l = r.getLong("updated") + (milliweeks * time);

                            if (l < System.currentTimeMillis()) {

                                today.add(r.getString("name"));

                            }
                        } while (r.next());

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

    }

    public static void loadRegions() {
        regions.clear();
        regionsReadable.clear();
        regionsUUID.clear();
        new BukkitRunnable() {

            @Override
            public void run() {

                try {
                    String statement = "SELECT * FROM mcmeproject_regions_data ;";

                    final ResultSet r = Mcproject.getPluginInstance().getSelectRegions().executeQuery();

                    if (r.first()) {
                        do {

                            if (r.getString("type").equalsIgnoreCase("cuboid")) {

                                String[] xlist = utils.unserialize(r.getString("xlist"));
                                String[] zlist = utils.unserialize(r.getString("zlist"));
                                String[] location = utils.unserialize(r.getString("location"));

                                Integer ymin = r.getInt("ymin");
                                Integer ymax = r.getInt("ymax");
                                Vector minCorner = new Vector(parseInt(xlist[0]),
                                        ymin,
                                        parseInt(zlist[0]));
                                Vector maxCorner = new Vector(parseInt(xlist[1]),
                                        ymax,
                                        parseInt(zlist[1]));

                                Location loc;

                                if (Mcproject.getPluginInstance().getNameserver().equalsIgnoreCase(r.getString("server"))) {
                                    loc = new Location(Bukkit.getWorld(location[0]), parseDouble(location[1]), parseDouble(location[2]), parseDouble(location[3]));
                                } else {
                                    loc = new Location(null,
                                            parseDouble(location[1]), parseDouble(location[2]), parseDouble(location[3]));
                                }
                                CuboidRegion rr = new CuboidRegion(loc, minCorner, maxCorner);

                                regions.put(r.getString("name"), new RegionData(r.getString("name"), UUID.fromString(r.getString("idregion")), UUID.fromString(r.getString("idproject")), rr, r.getString("server"), r.getString("type"), r.getInt("weight")));
                                regionsUUID.put(UUID.fromString(r.getString("idregion")), r.getString("name"));

                                if (regionsReadable.containsKey(UUID.fromString(r.getString("idproject")))) {
                                    List<String> s = regionsReadable.get(UUID.fromString(r.getString("idproject")));
                                    s.add(r.getString("name"));
                                    regionsReadable.remove(UUID.fromString(r.getString("idproject")));
                                    regionsReadable.put(UUID.fromString(r.getString("idproject")), s);
                                } else {
                                    List<String> l = new ArrayList();
                                    l.add(r.getString("name"));
                                    regionsReadable.put(UUID.fromString(r.getString("idproject")), l);
                                }

                            } else {

                                String[] location = utils.unserialize(r.getString("location"));
                                Integer ymin = r.getInt("ymin");
                                Integer ymax = r.getInt("ymax");
                                List<Integer> xlist = utils.StringtoListInt(utils.unserialize(r.getString("xlist")));
                                List<Integer> zlist = utils.StringtoListInt(utils.unserialize(r.getString("zlist")));

                                Location loc;

                                if (Mcproject.getPluginInstance().getNameserver().equalsIgnoreCase(r.getString("server"))) {
                                    loc = new Location(Bukkit.getWorld(location[0]), parseDouble(location[1]), parseDouble(location[2]), parseDouble(location[3]));
                                } else {
                                    loc = new Location(null,
                                            parseDouble(location[1]), parseDouble(location[2]), parseDouble(location[3]));
                                }
                                PrismoidRegion rr = new PrismoidRegion(loc, xlist, zlist, ymin, ymax);
                                regions.put(r.getString("name"), new RegionData(r.getString("name"), UUID.fromString(r.getString("idregion")), UUID.fromString(r.getString("idproject")), rr, r.getString("server"), r.getString("type"), r.getInt("weight")));
                                regionsUUID.put(UUID.fromString(r.getString("idregion")), r.getString("name"));

                                if (regionsReadable.containsKey(UUID.fromString(r.getString("idproject")))) {
                                    List<String> s = regionsReadable.get(UUID.fromString(r.getString("idproject")));
                                    s.add(r.getString("name"));
                                    regionsReadable.remove(UUID.fromString(r.getString("idproject")));
                                    regionsReadable.put(UUID.fromString(r.getString("idproject")), s);
                                } else {
                                    List<String> l = new ArrayList();
                                    l.add(r.getString("name"));
                                    regionsReadable.put(UUID.fromString(r.getString("idproject")), l);
                                }
                            }

                        } while (r.next());

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

    }

    public static void loadWarps() {
        warps.clear();

        new BukkitRunnable() {

            @Override
            public void run() {

                try {

                    final ResultSet r = Mcproject.getPluginInstance().getSelectWarps().executeQuery();

                    if (r.first()) {
                        do {
                            Location l;

                            if (Mcproject.getPluginInstance().getNameserver().equalsIgnoreCase(r.getString("server"))) {
                                l = new Location(Bukkit.getWorld(r.getString("world")),
                                        r.getFloat("x"), r.getFloat("y"), r.getFloat("z"));
                            } else {
                                l = new Location(null,
                                        r.getFloat("x"), r.getFloat("y"), r.getFloat("z"));
                            }
                            warps.put(UUID.fromString(r.getString("idregion")), new WarpData(UUID.fromString(r.getString("idproject")), UUID.fromString(r.getString("idregion")), l, r.getString("server"), r.getString("world")));

                        } while (r.next());

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

    }

    public static void loadProjects() {
        projectsAll.clear();
        projectsUUID.clear();
        new BukkitRunnable() {

            @Override
            public void run() {
                try {

                    final ResultSet r = Mcproject.getPluginInstance().getSelectProjects().executeQuery();

                    if (r.first()) {
                        do {

                            projectsAll.put(r.getString("name"), new ProjectData(r.getString("name"), UUID.fromString(r.getString("idproject")), ProjectStatus.valueOf(r.getString("status")), r.getBoolean("main"), utils.convertListString(utils.unserialize(r.getString("jobs"))), UUID.fromString(r.getString("staff_uuid")), r.getLong("time"), r.getInt("percentage"), r.getString("description"), r.getString("link"), r.getLong("updated"), r.getInt("minutes"), utils.convertListUUID(utils.unserialize(r.getString("assistants"))), utils.convertListUUID(utils.unserialize(r.getString("plcurrent"))), r.getInt("blocks")));
                            projectsUUID.put(UUID.fromString(r.getString("idproject")), r.getString("name"));
                        } while (r.next());

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

    }

    public static void sendNews(PlayerJoinEvent e) {

        final List<String> projects = new ArrayList<>();
        Player pl = e.getPlayer();
        new BukkitRunnable() {

            @Override
            public void run() {

                try {

                    Mcproject.getPluginInstance().getSelectNewsDataId().setString(1, e.getPlayer().getUniqueId().toString());
                    final ResultSet r = Mcproject.getPluginInstance().getSelectProjects().executeQuery();;

                    for (final String name : projectsAll.keySet()) {
                        int i = 0;

                        if (r.first()) {
                            do {

                                if (UUID.fromString(r.getString("idproject")).equals(projectsAll.get(name).getIdproject())) {
                                    i = 1;

                                }

                            } while (r.next());

                            if (i != 1) {
                                projects.add(name);

                            }
                        } else {
                            projects.add(name);

                        }
                    }

                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                    message.addSimple(ChatColor.GOLD + "Hi " + pl.getName() + ", welcome to MCME " + ChatColor.BLUE + projects.get(0) + "\n" + ChatColor.GOLD + "Currently we are working on these projects");

                    for (Entry<String, ProjectData> pr : PluginData.getProjectsAll().entrySet()) {

                        if (pr.getValue().getStatus() == ProjectStatus.SHOWED && projects.contains(pr.getKey())) {

                            message.addClickable(ChatColor.RED + "\n" + "+ " + pr.getKey() + ChatColor.GREEN + "[UPDATED]", "/project details " + projects.get(0)).setRunDirect();

                        } else if (pr.getValue().getStatus() == ProjectStatus.SHOWED && !projects.contains(pr.getKey())) {

                            message.addClickable(ChatColor.GREEN + "\n" + "+ " + pr.getKey(), "/project details " + projects.get(0)).setRunDirect();

                        }

                    }
                   
                    message.send(pl);

                } catch (SQLException ex) {
                    Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskAsynchronously(Mcproject.getPluginInstance());

    }

    public static void loadAllDynmap() {
        DynmapUtil.clearMarkersArea();
        DynmapUtil.clearMarkersWarp();

        regions.keySet().forEach((name) -> {
            RegionData s = regions.get(name);

            if (Mcproject.getPluginInstance().getNameserver().equalsIgnoreCase(s.getServer())) {
                if (!(projectsAll.get(projectsUUID.get(s.getIdproject())).getStatus().equals(ProjectStatus.FINISHED) || projectsAll.get(projectsUUID.get(s.getIdproject())).getStatus().equals(ProjectStatus.HIDDEN))) {
                    if (s.getType().equals("cuboid")) {
                        DynmapUtil.createMarkeronLoadCuboid(s.getName(), projectsUUID.get(s.getIdproject()), (CuboidRegion) s.getRegion());
                    } else {
                        DynmapUtil.createMarkeronLoad(s.getName(), projectsUUID.get(s.getIdproject()), (PrismoidRegion) s.getRegion());
                    }

                }
            }
        });

        warps.keySet().forEach((name) -> {
            WarpData s = warps.get(name);
            if (!(projectsAll.get(projectsUUID.get(s.getIdproject())).getStatus().equals(ProjectStatus.FINISHED) || projectsAll.get(projectsUUID.get(s.getIdproject())).getStatus().equals(ProjectStatus.HIDDEN))) {
                if (Mcproject.getPluginInstance().getNameserver().equalsIgnoreCase(s.getServer())) {
                    String n = regionsUUID.get(s.getIdregion()).toUpperCase() + " (" + projectsUUID.get(s.getIdproject()).toLowerCase() + ")";
                    DynmapUtil.createMarkerWarp(n, s.getLocation(), s.getWl());

                }
            }
        });

    }

}
