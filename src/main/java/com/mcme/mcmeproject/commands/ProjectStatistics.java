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
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import static java.lang.Integer.parseInt;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectStatistics extends ProjectCommand {

    public ProjectStatistics(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Get statistics about the server (Timezone London)");
        setUsageDescription(" today|week|month|custom: For custom you need also two dates with this format dd/mm/yyyy  ");
    }

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            Player pl = (Player) cs;
            if (args[0].equalsIgnoreCase("today")) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try {
                            Calendar cal = Calendar.getInstance();
                            int day = cal.get(Calendar.DAY_OF_MONTH);
                            int month = cal.get(Calendar.MONTH);
                            int year = cal.get(Calendar.YEAR);

                            String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_statistics_data WHERE day = '" + cal.get(Calendar.DAY_OF_MONTH) + "' AND month = '" + cal.get(Calendar.MONTH) + "' AND year = '" + cal.get(Calendar.YEAR) + "';";
                            ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                            if (r.first()) {
                                Logger.getGlobal().info("Oggi," + day + "/" + month + "/" + year);
                                int blocks = r.getInt("blocks");
                                int minutes = r.getInt("minutes");
                                List<UUID> plsUUID = PluginData.convertListUUID(PluginData.unserialize(r.getString("players")));
                                List<UUID> prsUUID = PluginData.convertListUUID(PluginData.unserialize(r.getString("projects")));
                                sendMessage(blocks, minutes, plsUUID.size(), prsUUID, "today", pl);
                            } else {
                                List<UUID> prsUUID = new ArrayList();
                                sendMessage(0, 0, 0, prsUUID, "today", pl);
                            }

                        } catch (SQLException ex) {

                            Logger.getLogger(ProjectStatistics.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

            } else if (args[0].equalsIgnoreCase("week")) {

                Long onewago = System.currentTimeMillis() - 604800000;

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(onewago);
                Calendar now = Calendar.getInstance();
                List<Calendar> listCal = createListDate(cal, now);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try {
                            Calendar cal = Calendar.getInstance();

                            String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_statistics_data ;";
                            ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                            int blocks = 0;
                            int minutes = 0;
                            List<UUID> plsUUID = new ArrayList<>();
                            List<UUID> prsUUID = new ArrayList<>();
                            for (Calendar firstDate : listCal) {
                                int day = firstDate.get(Calendar.DAY_OF_MONTH);
                                int month = firstDate.get(Calendar.MONTH);
                                int year = firstDate.get(Calendar.YEAR);

                                if (r.first()) {
                                    do {
                                        Logger.getGlobal().info("week" + day + "/" + month + "/" + year + ", " + r.getString("day") + "/" + r.getString("month") + "/" + r.getString("year"));

                                        if (r.getString("day").equalsIgnoreCase(String.valueOf(day))
                                                && r.getString("month").equalsIgnoreCase(String.valueOf(month))
                                                && r.getString("year").equalsIgnoreCase(String.valueOf(year))) {

                                            blocks = blocks + r.getInt("blocks");
                                            minutes = minutes + r.getInt("minutes");
                                            List<UUID> plsUUID2 = PluginData.convertListUUID(PluginData.unserialize(r.getString("players")));
                                            List<UUID> prsUUID2 = PluginData.convertListUUID(PluginData.unserialize(r.getString("projects")));

                                            for (UUID player : plsUUID2) {
                                                if (!plsUUID.contains(player)) {
                                                    plsUUID.add(player);
                                                }
                                            }
                                            for (UUID pr : prsUUID2) {
                                                if (!prsUUID.contains(pr)) {
                                                    prsUUID.add(pr);
                                                }
                                            }

                                        }

                                    } while (r.next());

                                }
                            }

                            sendMessage(blocks, minutes, plsUUID.size(), prsUUID, "week", pl);

                        } catch (SQLException ex) {

                            Logger.getLogger(ProjectStatistics.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

            } else if (args[0].equalsIgnoreCase("month")) {

                Long onemago = System.currentTimeMillis() - 2678400000L;

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(onemago);
                Calendar now = Calendar.getInstance();
                List<Calendar> listCal = createListDate(cal, now);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try {
                            Calendar cal = Calendar.getInstance();

                            String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_statistics_data ;";
                            ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                            if (r.first()) {

                                int blocks = 0;
                                int minutes = 0;
                                List<UUID> plsUUID = new ArrayList<>();
                                List<UUID> prsUUID = new ArrayList<>();
                                for (Calendar firstDate : listCal) {
                                    int day = firstDate.get(Calendar.DAY_OF_MONTH);
                                    int month = firstDate.get(Calendar.MONTH);
                                    int year = firstDate.get(Calendar.YEAR);
                                    if (r.first()) {
                                        do {
                                            System.out.println("month" + day + "/" + month + "/" + year + ", " + r.getString("day") + "/" + r.getString("month") + "/" + r.getString("year"));
                                            if (r.getString("day").equalsIgnoreCase(String.valueOf(day))
                                                    && r.getString("month").equalsIgnoreCase(String.valueOf(month))
                                                    && r.getString("year").equalsIgnoreCase(String.valueOf(year))) {

                                                blocks = blocks + r.getInt("blocks");
                                                minutes = minutes + r.getInt("minutes");
                                                List<UUID> plsUUID2 = PluginData.convertListUUID(PluginData.unserialize(r.getString("players")));
                                                List<UUID> prsUUID2 = PluginData.convertListUUID(PluginData.unserialize(r.getString("projects")));

                                                for (UUID player : plsUUID2) {
                                                    if (!plsUUID.contains(player)) {
                                                        plsUUID.add(player);
                                                    }
                                                }
                                                for (UUID pr : prsUUID2) {
                                                    if (!prsUUID.contains(pr)) {
                                                        prsUUID.add(pr);
                                                    }
                                                }

                                            }

                                        } while (r.next());

                                    }
                                }

                                sendMessage(blocks, minutes, plsUUID.size(), prsUUID, "month", pl);

                            } else {
                                sendErrorNoData(cs);
                            }

                        } catch (SQLException ex) {

                            Logger.getLogger(ProjectStatistics.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());
            } else if (args[0].equalsIgnoreCase("custom")) {
                if (validateDate(args[1])) {
                    if (validateDate(args[2])) {

                        Calendar first = Calendar.getInstance();
                        Calendar second = Calendar.getInstance();

                        String[] fir = unserialize(args[1]);
                        String[] sec = unserialize(args[2]);

                        first.set(parseInt(fir[2]), parseInt(fir[1]), parseInt(fir[0]));
                        second.set(parseInt(sec[2]), parseInt(sec[1]), parseInt(sec[0]));

                        List<Calendar> listCal = createListDate(first, second);
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                try {
                                    Calendar cal = Calendar.getInstance();

                                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_statistics_data ;";
                                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();
                                    if (r.first()) {

                                        int blocks = 0;
                                        int minutes = 0;
                                        List<UUID> plsUUID = new ArrayList<>();
                                        List<UUID> prsUUID = new ArrayList<>();
                                        for (Calendar firstDate : listCal) {
                                            int day = firstDate.get(Calendar.DAY_OF_MONTH);
                                            int month = firstDate.get(Calendar.MONTH);
                                            int year = firstDate.get(Calendar.YEAR);
                                            if (r.first()) {
                                                do {
                                                    System.out.println("custom" + day + "/" + month + "/" + year + ", " + r.getString("day") + "/" + r.getString("month") + "/" + r.getString("year"));
                                                    if (r.getString("day").equalsIgnoreCase(String.valueOf(day))
                                                            && r.getString("month").equalsIgnoreCase(String.valueOf(month))
                                                            && r.getString("year").equalsIgnoreCase(String.valueOf(year))) {

                                                        blocks = blocks + r.getInt("blocks");
                                                        minutes = minutes + r.getInt("minutes");
                                                        List<UUID> plsUUID2 = PluginData.convertListUUID(PluginData.unserialize(r.getString("players")));
                                                        List<UUID> prsUUID2 = PluginData.convertListUUID(PluginData.unserialize(r.getString("projects")));

                                                        for (UUID player : plsUUID2) {
                                                            if (!plsUUID.contains(player)) {
                                                                plsUUID.add(player);
                                                            }
                                                        }
                                                        for (UUID pr : prsUUID2) {
                                                            if (!prsUUID.contains(pr)) {
                                                                prsUUID.add(pr);
                                                            }
                                                        }

                                                    }

                                                } while (r.next());

                                            }
                                        }

                                        sendMessage(blocks, minutes, plsUUID.size(), prsUUID, args[1] + " to " + args[2], pl);

                                    } else {
                                        sendErrorNoData(cs);
                                    }

                                } catch (SQLException ex) {

                                    Logger.getLogger(ProjectStatistics.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    } else {
                        sendErrorSecondDate(cs);
                    }
                } else {
                    sendErrorFirstDate(cs);
                }
            } else {

                sendInvalidUsage(cs);
            }
        }

    }

    public static void sendMessage(int blocks, int minutes, int players, List<UUID> projects, String type, Player pl) {

        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
        message.addSimple(ChatColor.BOLD + "Period selected: '" + ChatColor.GOLD + type + "\n");
        message.addSimple(ChatColor.GREEN + "Hours of works: " + Math.round(minutes / 60) + "\n");
        message.addSimple(ChatColor.GREEN + "Total number of player: " + players + "\n");
        message.addSimple(ChatColor.GREEN + "Blocks : " + blocks + "\n");
        if (!projects.isEmpty()) {
            message.addSimple(ChatColor.GREEN + "Projects players worked on : ");
            for (UUID value : projects) {
                int index = projects.size() - 1;

                UUID val = projects.get(index);
                if (!value.equals(val)) {

                    message.addFancy(PluginData.projectsUUID.get(value) + ", ", "/project details" + PluginData.projectsUUID.get(value), PluginData.projectsAll.get(PluginData.projectsUUID.get(value)).description);
                } else {
                    message.addFancy(PluginData.projectsUUID.get(value) + "", "/project details" + PluginData.projectsUUID.get(value), PluginData.projectsAll.get(PluginData.projectsUUID.get(value)).description);
                }

            }
        }

        message.send(pl);

    }

    public List<Calendar> createListDate(Calendar first, Calendar second) {
        List<Calendar> datesInRange = new ArrayList<>();
        Calendar start = (Calendar) first.clone();
        datesInRange.add(second);
        while (start.before(second)) {
            datesInRange.add(start);
            start.add(Calendar.DAY_OF_MONTH, 1);
        }

        return datesInRange;
    }

    public boolean validateDate(String date) {
        String regex = "^([0-2][0-9]||3[0-1])/(0[0-9]||1[0-2])/([0-9][0-9])?[0-9][0-9]$";

        if (date.matches(regex)) {

            return true;

        } else {
            return false;
        }
    }

    public static String[] unserialize(String line) {
        String[] dataArray = line.split("/");

        return dataArray;

    }

    private void sendInvalidUsage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid usage, type /project help");
    }

    private void sendErrorNoData(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "No data available at the moment!");
    }

    private void sendErrorFirstDate(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "The first date format is not correct");
    }

    private void sendErrorSecondDate(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "The second date format is not correct");
    }

}
