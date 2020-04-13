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
import com.mcme.mcmeproject.data.PlayersData;
import com.mcme.mcmeproject.data.PluginData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

/**
 *
 * @author Fraspace5
 */
public class SystemRunnable {

    public static void startDatabaseRecoveryRunnable() {

        new BukkitRunnable() {

            @Override
            public void run() {
                PluginData.loadProjects();
                new BukkitRunnable() {

                    @Override
                    public void run() {

                        PluginData.loadRegions();
                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        PluginData.loadAllDynmap();
                                    }

                                }.runTaskLater(Mcproject.getPluginInstance(), 100L);

                                PluginData.loadWarps();
                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 60L);
                    }

                }.runTaskLater(Mcproject.getPluginInstance(), 100L);

            }

        }.runTaskTimer(Mcproject.getPluginInstance(), 100L, 36000L);

    }

    public static void variableDataMinutesRunnable() {

        new BukkitRunnable() {

            @Override
            public void run() {

                try {

                    if (!PluginData.getTemporaryMinute().isEmpty()) {
                        String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data ;";

                        final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                        if (r.first()) {
                            StringBuilder ss = new StringBuilder();
                            ss.append("UPDATE mcmeproject_project_data SET minutes = CASE idproject ");

                            do {

                                if (PluginData.getTemporaryMinute().containsKey(UUID.fromString(r.getString("idproject")))) {
                                    Integer i = r.getInt("minutes") + PluginData.getTemporaryMinute().get(UUID.fromString(r.getString("idproject")));

                                    ss.append(" WHEN '" + r.getString("idproject") + "' THEN '" + i.toString() + "' ");
                                }

                            } while (r.next());

                            ss.append("ELSE minutes END;");

                            Mcproject.getPluginInstance().con.prepareStatement(ss.toString()).executeUpdate();

                            PluginData.getTemporaryMinute().clear();
                        }

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskTimerAsynchronously(Mcproject.getPluginInstance(), 200L, 2400L);

    }

    public static void variableDataBlocksRunnable() {

        new BukkitRunnable() {

            @Override
            public void run() {

                try {

                    if (!PluginData.getAllblocks().isEmpty()) {
                        String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data ;";

                        final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                        if (r.first()) {
                            StringBuilder ss = new StringBuilder();
                            ss.append("UPDATE mcmeproject_project_data SET blocks = CASE idproject ");

                            do {
                                if (PluginData.getAllblocks().containsKey(UUID.fromString(r.getString("idproject")))) {
                                    Integer i = r.getInt("blocks") + PluginData.getAllblocks().get(UUID.fromString(r.getString("idproject")));

                                    ss.append(" WHEN '" + r.getString("idproject") + "' THEN '" + i.toString() + "' ");
                                }
                            } while (r.next());
                            ss.append("ELSE blocks END;");

                            Mcproject.getPluginInstance().con.prepareStatement(ss.toString()).executeUpdate();

                            PluginData.getAllblocks().clear();
                        }

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskTimerAsynchronously(Mcproject.getPluginInstance(), 300L, 2400L);

    }

    public static void PlayersDataBlocksRunnable() {
        HashMap<UUID, PlayersData> newlist = new HashMap();
        List<UUID> totalList = new ArrayList<>();
//idproject, idplayer
        new BukkitRunnable() {

            @Override
            public void run() {

                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_people_data ;";

                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                    for (UUID idplayer : PluginData.getTemporaryBlocks().keySet()) {

                        for (UUID idproject : PluginData.getTemporaryBlocks().get(idplayer).r.keySet()) {

                            if (!totalList.contains(idproject)) {
                                totalList.add(idproject);

                            }

                        }

                    }
                    if (r.first()) {
                        do {

                            if (newlist.containsKey(UUID.fromString(r.getString("player_uuid")))) {

                                if (!newlist.get(UUID.fromString(r.getString("player_uuid"))).r.containsKey(UUID.fromString(r.getString("idproject")))) {

                                    HashMap<UUID, Integer> s = newlist.get(UUID.fromString(r.getString("player_uuid"))).r;
                                    HashMap<UUID, Long> s2 = newlist.get(UUID.fromString(r.getString("player_uuid"))).lastplayed;
                                    s.put((UUID.fromString(r.getString("idproject"))), r.getInt("blocks"));
                                    s2.put(UUID.fromString(r.getString("idproject")), r.getLong("lastplayed"));
                                    newlist.remove(UUID.fromString(r.getString("player_uuid")));
                                    newlist.put(UUID.fromString(r.getString("player_uuid")), new PlayersData(s, s2));

                                }

                            } else {
                                HashMap<UUID, Integer> s = new HashMap();
                                HashMap<UUID, Long> s2 = new HashMap();
                                s.put(UUID.fromString(r.getString("idproject")), r.getInt("blocks"));
                                s2.put(UUID.fromString(r.getString("idproject")), r.getLong("lastplayed"));
                                newlist.put(UUID.fromString(r.getString("player_uuid")), new PlayersData(s, s2));

                            }

                        } while (r.next());
                    }

                    if (!totalList.isEmpty()) {
                        for (final UUID projectid : totalList) {
                            StringBuilder ss = new StringBuilder();
                            final StringBuilder pp = new StringBuilder();
                            ss.append("UPDATE mcmeproject_people_data SET blocks = CASE player_uuid  ");
                            pp.append("UPDATE mcmeproject_people_data SET lastplayed = CASE player_uuid  ");

                            Integer testCheck1 = 0;
                            Integer testCheck2 = 0;

                            for (UUID s : PluginData.getTemporaryBlocks().keySet()) {

                                if (PluginData.getTemporaryBlocks().get(s).r.containsKey(projectid)) {
                                    if (newlist.containsKey(s)) {
                                        if (newlist.get(s).r.containsKey(projectid)) {

                                            Integer nn = PluginData.getTemporaryBlocks().get(s).r.get(projectid) + newlist.get(s).r.get(projectid);
                                            ss.append("WHEN '" + s.toString() + "' THEN '" + nn.toString() + "'");
                                            testCheck1 = testCheck1 + 1;
                                            testCheck2 = testCheck2 + 1;
                                            pp.append("WHEN '" + s.toString() + "' THEN '" + PluginData.getTemporaryBlocks().get(s).lastplayed.get(projectid) + "'");

                                        }
                                    }
                                }

                            }

                            ss.append("ELSE blocks END WHERE idproject = '" + projectid.toString() + "' ;");
                            pp.append("ELSE lastplayed END WHERE idproject = '" + projectid.toString() + "' ;");

                            if (testCheck1 != 0) {
                                try {
                                    Mcproject.getPluginInstance().con.prepareStatement(ss.toString()).executeUpdate(ss.toString());

                                } catch (SQLException ex) {
                                    Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            if (testCheck2 != 0) {
                                try {
                                    Mcproject.getPluginInstance().con.prepareStatement(pp.toString()).executeUpdate(pp.toString());

                                } catch (SQLException ex) {
                                    Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        }
                    }

                    if (!totalList.isEmpty()) {
                        for (UUID projectid : totalList) {
                            StringBuilder ss = new StringBuilder();

                            ss.append("INSERT INTO mcmeproject_people_data (player_uuid, idproject, blocks, lastplayed) VALUES  ");
                            Integer testCheck1 = 0;

                            for (UUID s : PluginData.getTemporaryBlocks().keySet()) {

                                if (PluginData.getTemporaryBlocks().get(s).r.containsKey(projectid)) {
                                    if (newlist.containsKey(s)) {
                                        if (!newlist.get(s).r.containsKey(projectid)) {
                                            Integer nn = PluginData.getTemporaryBlocks().get(s).r.get(projectid);
                                            testCheck1 = testCheck1 + 1;
                                            ss.append(" ('" + s.toString() + "', '" + projectid.toString() + "','" + nn.toString() + "', '" + PluginData.getTemporaryBlocks().get(s).lastplayed.get(projectid) + "'),");

                                        }
                                    } else {
                                        Integer nn = PluginData.getTemporaryBlocks().get(s).r.get(projectid);

                                        ss.append(" ('" + s.toString() + "', '" + projectid.toString() + "','" + nn.toString() + "', '" + PluginData.getTemporaryBlocks().get(s).lastplayed.get(projectid) + "'),");
                                        testCheck1 = testCheck1 + 1;
                                    }
                                }

                            }

                            String text = ss.toString().substring(0, ss.toString().length() - 1) + (" ;");

                            if (testCheck1 != 0) {
                                Mcproject.getPluginInstance().con.prepareStatement(text).executeUpdate(text);
                            }

                        }
                    }

                    PluginData.getTemporaryBlocks().clear();
                } catch (SQLException ex) {
                    Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.runTaskTimerAsynchronously(Mcproject.getPluginInstance(), 400L, 1700L);

    }

    public static void statisticAllRunnable() {
        new BukkitRunnable() {

            @Override
            public void run() {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try {
                            Calendar cal = Calendar.getInstance();

                            String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_statistics_data WHERE day =" + cal.get(Calendar.DAY_OF_MONTH) + " AND month = " + cal.get(Calendar.MONTH) + " AND year =" + cal.get(Calendar.YEAR) + " ;";

                            final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                            if (PluginData.getTodayStat().containsKey("today")) {

                                if (r.first()) {
                                    List<UUID> play = PluginData.convertListUUID(PluginData.unserialize(r.getString("players")));
                                    List<UUID> projects = PluginData.convertListUUID(PluginData.unserialize(r.getString("projects")));
                                    for (UUID uuid : PluginData.getTodayStat().get("today").players) {

                                        if (!play.contains(uuid)) {

                                            play.add(uuid);

                                        }

                                    }
                                    for (UUID uuid : PluginData.getTodayStat().get("today").projects) {

                                        if (!projects.contains(uuid)) {

                                            projects.add(uuid);

                                        }

                                    }

                                    String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_statistics_data SET players = '" + serialize(play) + "', projects = '" + serialize(projects) + "', minutes = " + (r.getInt("minutes") + PluginData.getTodayStat().get("today").min) + ", blocks = " + (r.getInt("blocks") + PluginData.getTodayStat().get("today").blocks) + " WHERE day = '" + cal.get(Calendar.DAY_OF_MONTH) + "' AND month = '" + cal.get(Calendar.MONTH) + "' AND year = '" + cal.get(Calendar.YEAR) + "' ;";

                                    Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate();
                                    PluginData.getTodayStat().clear();
                                } else {

                                    String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".mcmeproject_statistics_data (day, month, year, blocks, minutes, players, projects) VALUES ('" + cal.get(Calendar.DAY_OF_MONTH) + "' ,'" + cal.get(Calendar.MONTH) + "' ,'" + cal.get(Calendar.YEAR) + "' ,'" + PluginData.getTodayStat().get("today").blocks + "' ,'" + PluginData.getTodayStat().get("today").min + "' ,'" + serialize(PluginData.getTodayStat().get("today").players) + "','" + serialize(PluginData.getTodayStat().get("today").projects) + "') ;";

                                    Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate();
                                    PluginData.getTodayStat().clear();
                                }
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(SystemRunnable.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());
            }

        }.runTaskTimer(Mcproject.getPluginInstance(), 1000L, 1200L);
        //sistema a 25
    }

    public static String serialize(List<UUID> intlist) {

        StringBuilder builder = new StringBuilder();
        if (!intlist.isEmpty()) {
            for (UUID s : intlist) {

                builder.append(s.toString()).append(";");

            }
        }
        return builder.toString();

    }

}
