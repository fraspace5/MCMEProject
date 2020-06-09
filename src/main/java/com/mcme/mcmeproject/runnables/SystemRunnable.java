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
import com.mcme.mcmeproject.util.utils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.scheduler.BukkitRunnable;

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
                                        if (!Mcproject.getPluginInstance().getNameserver().equals("default")) {
                                            PluginData.loadAllDynmap();

                                        }
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

    public static void ConnectionRunnable() {

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    if (!Mcproject.getPluginInstance().getConnection().isValid(5)) {

                        Mcproject.getPluginInstance().getConnection().close();
                        Mcproject.getPluginInstance().openConnection();

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Mcproject.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskTimerAsynchronously(Mcproject.getPluginInstance(), 150L, 1000L);

    }

    public static void variableDataMinutesRunnable() {

        new BukkitRunnable() {

            @Override
            public void run() {

                try {

                    if (!PluginData.getTemporaryMinute().isEmpty()) {
                        String statement = "SELECT * FROM mcmeproject_project_data ;";

                        Statement statm1 = Mcproject.getPluginInstance().getConnection().prepareStatement(statement);
                        statm1.setQueryTimeout(10);
                        final ResultSet r = statm1.executeQuery(statement);

                        if (r.first()) {
                            StringBuilder ss = new StringBuilder();
                            ss.append("UPDATE mcmeproject_project_data SET minutes = CASE idproject ");

                            do {

                                if (PluginData.getTemporaryMinute().containsKey(UUID.fromString(r.getString("idproject")))) {
                                    Integer i = r.getInt("minutes") + PluginData.getTemporaryMinute().get(UUID.fromString(r.getString("idproject")));

                                    ss.append(" WHEN '").append(r.getString("idproject")).append("' THEN '").append(i.toString()).append("' ");
                                }

                            } while (r.next());

                            ss.append("ELSE minutes END;");

                            Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(ss.toString());
                            statm.setQueryTimeout(10);
                            statm.executeUpdate(ss.toString());

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
                        String statement = "SELECT * FROM mcmeproject_project_data ;";

                        Statement statm1 = Mcproject.getPluginInstance().getConnection().prepareStatement(statement);
                        statm1.setQueryTimeout(10);
                        final ResultSet r = statm1.executeQuery(statement);

                        if (r.first()) {
                            StringBuilder ss = new StringBuilder();
                            ss.append("UPDATE mcmeproject_project_data SET blocks = CASE idproject ");

                            do {
                                if (PluginData.getAllblocks().containsKey(UUID.fromString(r.getString("idproject")))) {
                                    Integer i = r.getInt("blocks") + PluginData.getAllblocks().get(UUID.fromString(r.getString("idproject")));

                                    ss.append(" WHEN '").append(r.getString("idproject")).append("' THEN '").append(i.toString()).append("' ");
                                }
                            } while (r.next());
                            ss.append("ELSE blocks END;");

                            Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(ss.toString());
                            statm.setQueryTimeout(10);
                            statm.executeUpdate(ss.toString());

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
                    String statement = "SELECT * FROM mcmeproject_people_data ;";

                    Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(statement);
                    statm.setQueryTimeout(10);
                    final ResultSet r = statm.executeQuery(statement);
                    PluginData.getTemporaryBlocks().keySet().forEach((idplayer) -> {
                        PluginData.getTemporaryBlocks().get(idplayer).getR().keySet().forEach((idproject) -> {
                            if (!totalList.contains(idproject)) {
                                totalList.add(idproject);

                            }
                        });
                    });
                    if (r.first()) {
                        do {

                            if (newlist.containsKey(UUID.fromString(r.getString("player_uuid")))) {

                                if (!newlist.get(UUID.fromString(r.getString("player_uuid"))).getR().containsKey(UUID.fromString(r.getString("idproject")))) {

                                    HashMap<UUID, Integer> s = newlist.get(UUID.fromString(r.getString("player_uuid"))).getR();
                                    HashMap<UUID, Long> s2 = newlist.get(UUID.fromString(r.getString("player_uuid"))).getLastplayed();
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
                        totalList.forEach((projectid) -> {
                            StringBuilder ss = new StringBuilder();
                            final StringBuilder pp = new StringBuilder();
                            ss.append("UPDATE mcmeproject_people_data SET blocks = CASE player_uuid  ");
                            pp.append("UPDATE mcmeproject_people_data SET lastplayed = CASE player_uuid  ");
                            Integer testCheck1 = 0;
                            Integer testCheck2 = 0;
                            for (UUID s : PluginData.getTemporaryBlocks().keySet()) {

                                if (PluginData.getTemporaryBlocks().get(s).getR().containsKey(projectid)) {
                                    if (newlist.containsKey(s)) {
                                        if (newlist.get(s).getR().containsKey(projectid)) {

                                            Integer nn = PluginData.getTemporaryBlocks().get(s).getR().get(projectid) + newlist.get(s).getR().get(projectid);
                                            ss.append("WHEN '").append(s.toString()).append("' THEN '").append(nn.toString()).append("'");
                                            testCheck1 = testCheck1 + 1;
                                            testCheck2 = testCheck2 + 1;
                                            pp.append("WHEN '").append(s.toString()).append("' THEN '").append(PluginData.getTemporaryBlocks().get(s).getLastplayed().get(projectid)).append("'");

                                        }
                                    }
                                }

                            }
                            ss.append("ELSE blocks END WHERE idproject = '").append(projectid.toString()).append("' ;");
                            pp.append("ELSE lastplayed END WHERE idproject = '").append(projectid.toString()).append("' ;");
                            if (testCheck1 != 0) {
                                try {
                                    Statement statm1 = Mcproject.getPluginInstance().getConnection().prepareStatement(ss.toString());
                                    statm1.setQueryTimeout(10);
                                    statm1.executeUpdate(ss.toString());

                                } catch (SQLException ex) {
                                    Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            if (testCheck2 != 0) {
                                try {
                                    Statement statm1 = Mcproject.getPluginInstance().getConnection().prepareStatement(pp.toString());
                                    statm1.setQueryTimeout(10);
                                    statm1.executeUpdate(pp.toString());
                                } catch (SQLException ex) {
                                    Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                    }

                    if (!totalList.isEmpty()) {
                        for (UUID projectid : totalList) {
                            StringBuilder ss = new StringBuilder();

                            ss.append("INSERT INTO mcmeproject_people_data (player_uuid, idproject, blocks, lastplayed) VALUES  ");
                            Integer testCheck1 = 0;

                            for (UUID s : PluginData.getTemporaryBlocks().keySet()) {

                                if (PluginData.getTemporaryBlocks().get(s).getR().containsKey(projectid)) {
                                    if (newlist.containsKey(s)) {
                                        if (!newlist.get(s).getR().containsKey(projectid)) {
                                            Integer nn = PluginData.getTemporaryBlocks().get(s).getR().get(projectid);
                                            testCheck1 = testCheck1 + 1;
                                            ss.append(" ('").append(s.toString()).append("', '").append(projectid.toString()).append("','").append(nn.toString()).append("', '").append(PluginData.getTemporaryBlocks().get(s).getLastplayed().get(projectid)).append("'),");

                                        }
                                    } else {
                                        Integer nn = PluginData.getTemporaryBlocks().get(s).getR().get(projectid);

                                        ss.append(" ('").append(s.toString()).append("', '").append(projectid.toString()).append("','").append(nn.toString()).append("', '").append(PluginData.getTemporaryBlocks().get(s).getLastplayed().get(projectid)).append("'),");
                                        testCheck1 = testCheck1 + 1;
                                    }
                                }

                            }

                            String text = ss.toString().substring(0, ss.toString().length() - 1) + (" ;");

                            if (testCheck1 != 0) {
                                Statement statm1 = Mcproject.getPluginInstance().getConnection().prepareStatement(text);
                                statm1.setQueryTimeout(10);
                                statm1.executeUpdate(text);
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

                            String statement = "SELECT * FROM mcmeproject_statistics_data WHERE day =" + cal.get(Calendar.DAY_OF_MONTH) + " AND month = " + cal.get(Calendar.MONTH) + " AND year =" + cal.get(Calendar.YEAR) + " ;";

                            Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(statement);
                            statm.setQueryTimeout(10);
                            final ResultSet r = statm.executeQuery(statement);
                            if (PluginData.getTodayStat().containsKey("today")) {

                                if (r.first()) {
                                    List<UUID> play = utils.convertListUUID(utils.unserialize(r.getString("players")));
                                    List<UUID> projects = utils.convertListUUID(utils.unserialize(r.getString("projects")));
                                    PluginData.getTodayStat().get("today").getPlayers().forEach((uuid) -> {
                                        if (!play.contains(uuid)) {

                                            play.add(uuid);

                                        }
                                    });
                                    PluginData.getTodayStat().get("today").getProjects().forEach((uuid) -> {
                                        if (!projects.contains(uuid)) {

                                            projects.add(uuid);

                                        }
                                    });

                                    String stat = "UPDATE mcmeproject_statistics_data SET players = '" + serialize(play) + "', projects = '" + serialize(projects) + "', minutes = " + (r.getInt("minutes") + PluginData.getTodayStat().get("today").getMin()) + ", blocks = " + (r.getInt("blocks") + PluginData.getTodayStat().get("today").getBlocks()) + " WHERE day = '" + cal.get(Calendar.DAY_OF_MONTH) + "' AND month = '" + cal.get(Calendar.MONTH) + "' AND year = '" + cal.get(Calendar.YEAR) + "' ;";

                                    Statement statm1 = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                    statm1.setQueryTimeout(10);
                                    statm1.executeUpdate(stat);

                                    PluginData.getTodayStat().clear();
                                } else {

                                    String stat = "INSERT INTO mcmeproject_statistics_data (day, month, year, blocks, minutes, players, projects) VALUES ('" + cal.get(Calendar.DAY_OF_MONTH) + "' ,'" + cal.get(Calendar.MONTH) + "' ,'" + cal.get(Calendar.YEAR) + "' ,'" + PluginData.getTodayStat().get("today").getBlocks() + "' ,'" + PluginData.getTodayStat().get("today").getMin() + "' ,'" + serialize(PluginData.getTodayStat().get("today").getPlayers()) + "','" + serialize(PluginData.getTodayStat().get("today").getProjects()) + "') ;";

                                    Statement statm1 = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                    statm1.setQueryTimeout(10);
                                    statm1.executeUpdate(stat);

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
    }

    private static String serialize(List<UUID> intlist) {

        StringBuilder builder = new StringBuilder();
        if (!intlist.isEmpty()) {
            intlist.forEach((s) -> {
                builder.append(s.toString()).append(";");
            });
        }
        return builder.toString();

    }

}

/*
 Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                    statm.setQueryTimeout(10);
                                    statm.executeUpdate(stat);

 Statement statm = Mcproject.getPluginInstance().getConnection().prepareStatement(stat);
                                    statm.setQueryTimeout(10);
                                 final ResultSet r =  statm.executeUpdate(stat);


 */
