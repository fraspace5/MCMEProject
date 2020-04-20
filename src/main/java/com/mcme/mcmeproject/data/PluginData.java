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
    public static Map<String, ProjectData> projectsAll = new HashMap<>();
    @Getter
    public static Map<UUID, String> projectsUUID = new HashMap<>();
    
    @Getter
    public static Map<String, RegionData> regions = new HashMap<>();
    //name region

    @Getter
    public static Map<UUID, String> regionsUUID = new HashMap<>();
    @Getter
    @Setter
    public static Map<UUID, List<String>> regionsReadable = new HashMap<>();
    
    @Getter
    public static Map<UUID, WarpData> warps = new HashMap<>();
    
    @Getter
    public static Map<UUID, List<UUID>> informedRegion = new HashMap<>();
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
    
    public static String serialize(UUID uuid, Boolean bool) {
        return uuid + ";" + bool;
    }
    
    public static String[] unserialize(String line) {
        String[] dataArray = line.split(";");
        
        return dataArray;
        
    }
    
    public static void setTodayEnd() {
        today.clear();
        
        new BukkitRunnable() {
            
            @Override
            public void run() {
                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data ;";
                    
                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
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
    
    public static UUID createId() {
        
        UUID uuid = UUID.randomUUID();
        
        return uuid;
        
    }
    
    public static void loadRegions() {
        regions.clear();
        regionsReadable.clear();
        regionsUUID.clear();
        new BukkitRunnable() {
            
            @Override
            public void run() {
                
                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_regions_data ;";
                    
                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                    
                    if (r.first()) {
                        do {
                            
                            if (r.getString("type").equalsIgnoreCase("cuboid")) {
                                
                                String[] xlist = unserialize(r.getString("xlist"));
                                String[] zlist = unserialize(r.getString("zlist"));
                                String[] location = unserialize(r.getString("location"));
                                
                                Integer ymin = r.getInt("ymin");
                                Integer ymax = r.getInt("ymax");
                                Vector minCorner = new Vector(parseInt(xlist[0]),
                                        ymin,
                                        parseInt(zlist[0]));
                                Vector maxCorner = new Vector(parseInt(xlist[1]),
                                        ymax,
                                        parseInt(zlist[1]));
                                
                                Location loc = new Location(Bukkit.getWorld(UUID.fromString(location[0])), parseDouble(location[1]), parseDouble(location[2]), parseDouble(location[3]));
                                
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
                                
                                String[] xl = unserialize(r.getString("xlist"));
                                String[] zl = unserialize(r.getString("zlist"));
                                String[] location = unserialize(r.getString("location"));
                                Integer ymin = r.getInt("ymin");
                                Integer ymax = r.getInt("ymax");
                                List<Integer> xlist = StringtoListInt(unserialize(r.getString("xlist")));
                                List<Integer> zlist = StringtoListInt(unserialize(r.getString("zlist")));
                                Location loc = new Location(Bukkit.getWorld(UUID.fromString(location[0])), parseDouble(location[1]), parseDouble(location[2]), parseDouble(location[3]));
                                
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
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_warps_data ;";
                    
                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                    
                    if (r.first()) {
                        do {
                            
                            Location loc = new Location(Bukkit.getWorld(UUID.fromString(r.getString("world"))), r.getFloat("x"), r.getFloat("y"), r.getFloat("z"));
                            
                            warps.put(UUID.fromString(r.getString("idregion")), new WarpData(UUID.fromString(r.getString("idproject")), UUID.fromString(r.getString("idregion")), loc, r.getString("server"),r.getString("world")));
                            
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
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data ;";
                    
                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                    
                    if (r.first()) {
                        do {
                            
                            projectsAll.put(r.getString("name"), new ProjectData(r.getString("name"), UUID.fromString(r.getString("idproject")), ProjectStatus.valueOf(r.getString("status")), r.getBoolean("main"), convertListString(unserialize(r.getString("jobs"))), UUID.fromString(r.getString("staff_uuid")), r.getLong("time"), r.getInt("percentage"), r.getString("description"), r.getString("link"), r.getLong("updated"), r.getInt("minutes"), convertListUUID(unserialize(r.getString("assistants"))), convertListUUID(unserialize(r.getString("plcurrent"))), r.getInt("blocks")));
                            projectsUUID.put(UUID.fromString(r.getString("idproject")), r.getString("name"));
                        } while (r.next());
                        
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }.runTaskAsynchronously(Mcproject.getPluginInstance());
        
    }

    /*
    
     new BukkitRunnable() {
            
     @Override
     public void run() {
                
                
     }
            
     }.runTaskAsynchronously(Mcproject.getPluginInstance());
    
    
     */
    public static void sendNews(PlayerJoinEvent e) {
        
        final List<String> projects = new ArrayList<>();
        Player pl = e.getPlayer();
        new BukkitRunnable() {
            
            @Override
            public void run() {
                
                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_news_data WHERE player_uuid = '" + e.getPlayer().getUniqueId().toString() + "' ;";
                    
                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                    
                    for (final String name : projectsAll.keySet()) {
                        int i = 0;
                        
                        if (r.first()) {
                            do {
                                
                                if (UUID.fromString(r.getString("idproject")).equals(projectsAll.get(name).idproject)) {
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
                    
                    if (projects.size() == 1) {
                        
                        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                        
                        message.addSimple(ChatColor.GOLD + "Hi " + pl.getName() + ", project " + ChatColor.BLUE + projects.get(0) + ChatColor.GOLD + " has been updated ");
                        
                        message.addClickable(ChatColor.GREEN + "\n" + "Click here for more information", "/project details " + projects.get(0)).setRunDirect();
                        
                        message.send(pl);
                        
                    } else if (projects.size() > 1) {
                        
                        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                        
                        message.addSimple(ChatColor.GOLD + "Hi " + pl.getName() + ",some projects " + ChatColor.GOLD + " have been updated: ");
                        
                        for (String n : projects) {
                            int lastindex = projects.size() - 1;
                            
                            if (projects.indexOf(n) == 0) {
                                
                                message.addFancy(ChatColor.GREEN + "\n" + n + ",", "/project details " + n, "Click for more information about this project").setRunDirect();
                                
                            } else if (projects.indexOf(n) == lastindex) {
                                
                                message.addFancy(ChatColor.GREEN + n + ".", "/project details " + n, "Click for more information about this project").setRunDirect();
                                
                            } else {
                                message.addFancy(ChatColor.GREEN + n + ",", "/project details " + n, "Click for more information about this project").setRunDirect();
                            }
                            
                        }
                        
                        message.send(pl);
                        
                    }
                    
                } catch (SQLException ex) {
                    Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
        }.runTaskAsynchronously(Mcproject.getPluginInstance());
        
    }
    
    public static List<Integer> StringtoListInt(String[] s) {
        
        List<Integer> list = new ArrayList();
        
        for (int i = 0; i < s.length; i++) {
            list.add(Integer.parseInt(s[i]));
        }
        return list;
    }
    
    public static List<String> convertListString(String[] s) {
        
        List<String> list = new ArrayList();
        
        for (int i = 0; i < s.length; i++) {
            list.add(s[i]);
        }
        return list;
    }
    
    public static List<UUID> convertListUUID(String[] s) {
        
        List<UUID> list = new ArrayList();
        
        for (int i = 0; i < s.length; i++) {
            try {
                list.add(UUID.fromString(s[i]));
                
            } catch (IllegalArgumentException exception) {
                
            }
            
        }
        return list;
    }
    
    public static Integer getInt(ResultSet r, UUID projectid, UUID playerid) throws SQLException {
        if (r.first()) {
            do {
                
                if (r.getString("idproject").equals(projectid.toString()) && r.getString("player_uuid").equals(playerid.toString())) {
                    
                    return r.getInt("blocks");
                    
                } else {
                    
                    return 0;
                }
                
            } while (r.next());
        } else {
            
            return 0;
        }
        
    }
    
    public static void loadAllDynmap() {
        DynmapUtil.clearMarkersArea();
        DynmapUtil.clearMarkersWarp();
        
        for (String name : regions.keySet()) {
            
            RegionData s = regions.get(name);
            
            if (Bukkit.getWorlds().contains(s.region.getWorld()) && Mcproject.getPluginInstance().nameserver.equalsIgnoreCase(s.server)) {
                if (!(projectsAll.get(projectsUUID.get(s.idproject)).status.equals(ProjectStatus.FINISHED) || projectsAll.get(projectsUUID.get(s.idproject)).status.equals(ProjectStatus.HIDDEN))) {
                    if (s.type.equals("cuboid")) {
                        DynmapUtil.createMarkeronLoadCuboid(s.name, projectsUUID.get(s.idproject), (CuboidRegion) s.region);
                    } else {
                        DynmapUtil.createMarkeronLoad(s.name, projectsUUID.get(s.idproject), (PrismoidRegion) s.region);
                    }
                    
                }
            }
            
        }
        
        for (UUID name : warps.keySet()) {
            WarpData s = warps.get(name);
            if (!(projectsAll.get(projectsUUID.get(s.idproject)).status.equals(ProjectStatus.FINISHED) || projectsAll.get(projectsUUID.get(s.idproject)).status.equals(ProjectStatus.HIDDEN))) {
                if (Bukkit.getWorlds().contains(s.wl) && Mcproject.getPluginInstance().nameserver.equalsIgnoreCase(s.server)) {
                    String n = regionsUUID.get(s.idregion).toUpperCase() + " (" + projectsUUID.get(s.idproject).toLowerCase() + ")";
                    DynmapUtil.createMarkerWarp(n, s.location);
                    
                }
            }
        }
        
    }
    
}
