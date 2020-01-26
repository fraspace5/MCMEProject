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
import com.mcme.mcmeproject.data.PluginData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
                        String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".project_data ;";

                        final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                        StringBuilder ss = new StringBuilder();
                        ss.append("UPDATE project_data SET minutes = CASE idproject ");

                        for (UUID id : PluginData.getTemporaryMinute().keySet()) {
                            if (r.first()) {
                                do {

                                    if (UUID.fromString(r.getString("idproject")).equals(id)) {
                                        Integer i = r.getInt("minutes") + PluginData.getTemporaryMinute().get(id);

                                        ss.append(" WHEN '" + id.toString() + "' THEN '" + i.toString() + "' ");
                                    }

                                } while (r.next());
                            }
                        }
                        ss.append(" ELSE minutes END ;");

                        Mcproject.getPluginInstance().con.prepareStatement(ss.toString()).executeUpdate();

                        PluginData.getTemporaryMinute().clear();

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskTimerAsynchronously(Mcproject.getPluginInstance(), 200L, 1200L);

    }

    public static void variableDataBlocksRunnable() {
        final HashMap<UUID, UUID> insertlist = new HashMap();
        final HashMap<UUID, UUID> updatelist = new HashMap();
        final List<UUID> totalList = new ArrayList();
        //idproject, idplayer
        new BukkitRunnable() {

            @Override
            public void run() {

                try {
                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".people_data;";

                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                    for (UUID idplayer : PluginData.getTemporaryBlocks().keySet()) {

                        for (UUID idproject : PluginData.getTemporaryBlocks().get(idplayer).r.keySet()) {
                            if (r.first()) {
                                do {

                                    if (UUID.fromString(r.getString("idproject")).equals(idproject) && r.getString("player_uuid").equals(idplayer.toString())) {
                                        updatelist.put(idproject, idplayer);
                                        if (!totalList.contains(idproject)) {
                                            totalList.add(idproject);
                                        }
                                    } else if (UUID.fromString(r.getString("idproject")).equals(idproject) && !r.getString("player_uuid").equals(idplayer.toString())) {
                                        insertlist.put(idproject, idplayer);
                                        if (!totalList.contains(idproject)) {
                                            totalList.add(idproject);
                                        }
                                    }

                                } while (r.next());
                            }

                        }

                    }
                    if (!totalList.isEmpty() && !updatelist.isEmpty()) {
                        for (final UUID projectid : totalList) {
                            StringBuilder ss = new StringBuilder();
                            final StringBuilder pp = new StringBuilder();
                            ss.append("UPDATE people_data SET blocks = CASE player_uuid  ");
                            pp.append("UPDATE people_data SET lastplayed = CASE player_uuid  ");
                            for (UUID pid : updatelist.keySet()) {

                                if (projectid.equals(pid)) {
                                    Integer nn = PluginData.getTemporaryBlocks().get(updatelist.get(pid)).r.get(projectid) + PluginData.getInt(r, projectid, updatelist.get(pid));
                                    ss.append("WHEN '" + updatelist.get(pid).toString() + "' THEN '" + nn.toString() + "'");
                                    pp.append("WHEN '" + updatelist.get(pid).toString() + "' THEN '" + PluginData.getTemporaryBlocks().get(updatelist.get(pid)).lastplayed + "'");
                                }
                            }

                            ss.append("ELSE blocks END WHERE idproject = " + projectid.toString() + ";");
                            pp.append("ELSE lastplayed END WHERE idproject = " + projectid.toString() + ";");
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    try {
                                        Mcproject.getPluginInstance().con.prepareStatement(pp.toString()).executeUpdate();
                                    } catch (SQLException ex) {
                                        Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }

                            }.runTaskLaterAsynchronously(Mcproject.getPluginInstance(), 20L);
                            Mcproject.getPluginInstance().con.prepareStatement(ss.toString()).executeUpdate();

                        }
                    }
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (!totalList.isEmpty() && !insertlist.isEmpty()) {
                                for (UUID projectid : totalList) {
                                    StringBuilder ss = new StringBuilder();
                                    ss.append("INSERT INTO people_data (player_uuid, idproject, blocks, lastplayed) VALUES  ");

                                    for (UUID pid : insertlist.keySet()) {

                                        if (projectid.equals(pid)) {
                                            Integer nn = PluginData.getTemporaryBlocks().get(updatelist.get(pid)).r.get(projectid);

                                            if (insertlist.size() == 1) {
                                                ss.append("('" + insertlist.get(pid).toString() + "', '" + projectid.toString() + "','" + nn.toString() + "', '" + PluginData.getTemporaryBlocks().get(updatelist.get(pid)).lastplayed + "')");
                                            } else {
                                                ss.append("('" + insertlist.get(pid).toString() + "', '" + projectid.toString() + "','" + nn.toString() + "', '" + PluginData.getTemporaryBlocks().get(updatelist.get(pid)).lastplayed + "')");
                                                insertlist.remove(pid);
                                            }

                                        }
                                    }

                                    ss.append("ELSE blocks END WHERE idproject = " + projectid.toString() + " ;");

                                    try {
                                        Mcproject.getPluginInstance().con.prepareStatement(ss.toString()).executeUpdate();
                                    } catch (SQLException ex) {
                                        Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                            }

                        }

                    }.runTaskLaterAsynchronously(Mcproject.getPluginInstance(), 40L);

                    PluginData.getTemporaryMinute().clear();

                } catch (SQLException ex) {
                    Logger.getLogger(SystemRunnable.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }.runTaskTimerAsynchronously(Mcproject.getPluginInstance(), 200L, 2400L);

    }

}
