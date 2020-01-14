/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.data;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import lombok.Getter;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Long.parseLong;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import static java.lang.Integer.parseInt;
import static java.lang.Double.parseDouble;
import com.mcmiddleearth.pluginutil.region.CuboidRegion;
import org.bukkit.Location;
import com.mcmiddleearth.pluginutil.region.PrismoidRegion;

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
    public static Map<String, ProjectGotData> projectsAll = new HashMap<>();

    @Getter
    public static Map<UUID, RegionData> regions = new HashMap<>();

    @Getter
    public static Map<UUID, WarpData> warps = new HashMap<>();

    @Getter
    private static Long time = Mcproject.getPluginInstance().getConfig().getLong("time");
    @Getter
    private static Boolean playernotification = Mcproject.getPluginInstance().getConfig().getBoolean("playernotification");
    @Getter
    private static Boolean main = Mcproject.getPluginInstance().getConfig().getBoolean("mainworld");
    @Getter
    private static Map<UUID, Boolean> min = new HashMap<>();

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
        final Calendar cal = Calendar.getInstance();
        final Calendar now = Calendar.getInstance();
        for (final String name : projectsAll.keySet()) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    try {
                        String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".project_data WHERE idproject =" + projectsAll.get(name).idproject.toString() + " ;";

                        final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                        if (r.first()) {

                            Integer milliweeks = 1000 * 60 * 60 * 24 * 7;

                            Long l = r.getLong("updated") + (milliweeks * time);

                            cal.setTimeInMillis(l);
                            int d = cal.get(Calendar.DAY_OF_MONTH);
                            int m = cal.get(Calendar.MONTH);
                            int dd = now.get(Calendar.DAY_OF_MONTH);
                            int mm = now.get(Calendar.MONTH);

                            if (d == dd && m == mm) {

                                today.add(r.getString("name"));

                            }

                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }.runTaskAsynchronously(Mcproject.getPluginInstance());

        }

    }

    public static UUID createId() {

        UUID uuid = UUID.randomUUID();

        return uuid;

    }

    public static void loadRegions() {
        regions.clear();

        new BukkitRunnable() {

            @Override
            public void run() {

                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".regions_data ;";

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

                                regions.put(UUID.fromString(r.getString("idproject")), new RegionData(r.getString("name"), UUID.fromString(r.getString("idregion")), UUID.fromString(r.getString("idproject")), rr));

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
                                regions.put(UUID.fromString(r.getString("idproject")), new RegionData(r.getString("name"), UUID.fromString(r.getString("idregion")), UUID.fromString(r.getString("idproject")), rr));
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
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".warps_data ;";

                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                    if (r.first()) {
                        do {

                            Location loc = new Location(Bukkit.getWorld(UUID.fromString(r.getString("world"))), r.getFloat("x"), r.getFloat("y"), r.getFloat("z"));

                            warps.put(UUID.fromString(r.getString("idproject")), new WarpData(UUID.fromString(r.getString("idproject")), UUID.fromString(r.getString("idregion")), loc));

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
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".project_data ;";

                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                    if (r.first()) {
                        do {

                            projectsAll.put(r.getString("name"), new ProjectGotData(r.getString("name"), UUID.fromString(r.getString("idproject")), ProjectStatus.valueOf(r.getString("status")), r.getBoolean("main")));

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
    public static void sendNews(Player pl) {

        final List<String> projects = new ArrayList<>();

        for (final String name : projectsAll.keySet()) {

            new BukkitRunnable() {

                @Override
                public void run() {

                    try {
                        String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".news_data WHERE idproject =" + projectsAll.get(name).idproject.toString() + " ;";

                        final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                        if (!r.first()) {

                            projects.add(name);

                        }

                    } catch (SQLException ex) {
                        Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }.runTaskAsynchronously(Mcproject.getPluginInstance());

        }

        if (projects.size() == 1) {

            FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());

            message.addSimple(ChatColor.GOLD + "Hi " + pl.getName() + ", project" + ChatColor.BLUE + projects.get(0) + ChatColor.GOLD + " has been updated ");

            message.addClickable(ChatColor.GREEN + "\n" + "Click here for more information", "/project details " + projects.get(0)).setRunDirect();

            message.send(pl);

        } else if (projects.size() > 1) {

            FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());

            message.addSimple(ChatColor.GOLD + "Hi " + pl.getName() + ",some projects" + ChatColor.GOLD + " have been updated: ");

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

        }
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

}
