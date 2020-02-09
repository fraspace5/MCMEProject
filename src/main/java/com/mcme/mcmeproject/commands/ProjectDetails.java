/*
 Copyright (C) 2020 MCME (Fraspace5)
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
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.thegaffer.storage.JobDatabase;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectDetails extends ProjectCommand {

    public ProjectDetails(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Show details and statistics about a project");
        setUsageDescription(" <ProjectName> : Show statistics and other informations about a project");
    }

    private static final List<String> pers = new ArrayList<>();

    private static final List<String> jobs = new ArrayList<>();

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            final Player pl = (Player) cs;

            if (PluginData.projectsAll.containsKey(args[0])) {

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        try {
                            String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_news_data WHERE player_uuid = '" + pl.getUniqueId().toString() + "' AND idproject = '" + PluginData.projectsAll.get(args[0]).idproject.toString() + "' ;";

                            final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                            if (!r.first()) {
                                String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".mcmeproject_news_data (idproject, player_uuid) VALUES ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','" + pl.getUniqueId().toString() + "') ;";
                                Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate();
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(ProjectDetails.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                if (!pl.hasPermission("project.manager") || !pl.hasPermission("project.owner")) {
                    if (PluginData.projectsAll.get(args[0]).status.equals(ProjectStatus.SHOWED)) {

                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                try {
                                    ProjectData pr = PluginData.projectsAll.get(args[0]);

                                    String stat2 = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_people_data WHERE idproject = '" + pr.idproject.toString() + "' ;";

                                    final ResultSet r2 = Mcproject.getPluginInstance().con.prepareStatement(stat2).executeQuery();

                                    Long r = (pr.time - System.currentTimeMillis());

                                    //seconds
                                    FancyMessage header = new FancyMessage(MessageType.INFO, PluginData.getMessageUtil())
                                            .addSimple("Informations about " + pr.name);
                                    List<FancyMessage> messages = new ArrayList<>();

                                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                    String ps = Bukkit.getOfflinePlayer(pr.head).getName();
                                    if (pr.main) {
                                        message.addSimple(ChatColor.DARK_RED + "Main Project of the Server");
                                        message.addSimple(ChatColor.GOLD.BOLD + "Project name: " + pr.name + "\n"
                                                + ChatColor.RED.BOLD + "Project Leader: " + ps + "\n"
                                                + ChatColor.GOLD + pr.description + "\n"
                                                + ChatColor.DARK_PURPLE + "-Assistants: " + tt(pr.assistants) + "\n"
                                                + ChatColor.GOLD + "+--------------------+" + "\n"
                                                + ChatColor.GREEN + "-Current percentage: " + pr.percentage.toString() + "%" + "\n"
                                                + ChatColor.GREEN + "-Extimated Time: " + time(r) + "\n"
                                                + ChatColor.GOLD + "-Other statistics:" + "\n"
                                                + ChatColor.GREEN + "-Hours of work: " + Math.round(pr.minutes / 60) + "\n"
                                                + ChatColor.GREEN + "-People that works on: " + people(r2) + "\n"
                                                + ChatColor.GOLD + "+--------------------+"
                                        );
                                    } else {

                                        message.addSimple(ChatColor.GOLD.BOLD + "Project name: " + pr.name + "\n"
                                                + ChatColor.RED.BOLD + "Project Leader: " + ps + "\n"
                                                + ChatColor.GOLD + pr.description + "\n"
                                                + ChatColor.DARK_PURPLE + "-Assistants: " + tt(pr.assistants) + "\n"
                                                + ChatColor.GOLD + "+--------------------+" + "\n"
                                                + ChatColor.GREEN + "-Current percentage: " + pr.percentage.toString() + "%" + "\n"
                                                + ChatColor.GREEN + "-Extimated Time: " + time(r) + "\n"
                                                + ChatColor.GOLD + "-Other statistics:" + "\n"
                                                + ChatColor.GREEN + "-Hours of work: " + Math.round(pr.minutes / 60) + "\n"
                                                + ChatColor.GREEN + "-People that works on: " + people(r2) + "\n"
                                                + ChatColor.GOLD + "+--------------------+"
                                        );
                                    }
                                    jj(pr);

                                    if (!jobs.isEmpty()) {
                                        message.addSimple(ChatColor.AQUA + "\n" + "Jobs linked to this project: " + "\n" + job());

                                        message.addSimple("\n" + ChatColor.GOLD + "+--------------------+");
                                    } else {
                                        message.addSimple(ChatColor.RED + "\n" + "No jobs linked to this project");
                                        message.addSimple("\n" + ChatColor.GOLD + "+--------------------+");
                                    }

                                    for (String region : PluginData.regionsReadable.get(PluginData.projectsAll.get(args[0]).idproject)) {

                                        message.addSimple("\n" + ChatColor.AQUA + region.toUpperCase() + ": ");
                                        if (PluginData.warps.containsKey(PluginData.regions.get(region).idr)) {
                                            message.addClickable(ChatColor.GREEN.UNDERLINE + "Click to teleport", "/project warp " + pr.name + " " + region).setRunDirect();

                                        } else {
                                            message.addSimple(ChatColor.RED + "No warp available for this region");

                                        }

                                        messages.add(message);

                                    }
                                    if (!pr.link.equalsIgnoreCase("Nothing")) {
                                        message.addFancy("\n" + ChatColor.LIGHT_PURPLE + "-Forum Thread", pr.link, "Click to go on the forum");
                                    }

                                    messages.add(message);

                                    PluginData.getMessageUtil().sendFancyListMessage((Player) cs, header, messages, "/project details " + pr.name, 1);

                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectDetails.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    } else {

                        sendProjectError(cs);

                    }
                } else {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            try {
                                ProjectData pr = PluginData.projectsAll.get(args[0]);

                                String stat2 = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_people_data WHERE idproject = '" + pr.idproject.toString() + "' ;";

                                final ResultSet r2 = Mcproject.getPluginInstance().con.prepareStatement(stat2).executeQuery();

                                Long r = (pr.time - System.currentTimeMillis());

                                //seconds
                                FancyMessage header = new FancyMessage(MessageType.INFO, PluginData.getMessageUtil())
                                        .addSimple("Informations about " + pr.name);
                                List<FancyMessage> messages = new ArrayList<>();

                                FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                String ps = Bukkit.getOfflinePlayer(pr.head).getName();
                                if (pr.status.equals(ProjectStatus.FINISHED)) {
                                    if (pr.main) {
                                        message.addSimple(ChatColor.DARK_RED + "Main Project of the Server");
                                    }
                                    message.addSimple(ChatColor.BOLD.GOLD + "PROJECT: " + pr.name + " (Finished)" + "\n"
                                            + ChatColor.RED.BOLD + "Project Leader: " + ps + "\n"
                                            + ChatColor.GOLD + pr.description + "\n"
                                            + ChatColor.DARK_PURPLE + "Assistants: " + tt(pr.assistants) + "\n"
                                            + ChatColor.GOLD + "+--------------------+" + "\n"
                                            + ChatColor.GREEN + "Current percentage: " + pr.percentage.toString() + "%" + "\n"
                                            + ChatColor.GOLD + "Other statistics:" + "\n"
                                            + ChatColor.GREEN + "Hours of work: " + Math.round(pr.minutes / 60) + "\n"
                                            + ChatColor.GOLD + "+--------------------+"
                                    );

                                } else if (pr.status.equals(ProjectStatus.HIDDEN)) {
                                    if (pr.main) {
                                        message.addSimple(ChatColor.DARK_RED + "Main Project of the Server");
                                    }
                                    message.addSimple(ChatColor.BOLD.GOLD + "PROJECT: " + pr.name + " (Hidden)" + "\n"
                                            + ChatColor.RED.BOLD + "Project Leader: " + ps + "\n"
                                            + ChatColor.GOLD + pr.description + "\n"
                                            + ChatColor.DARK_PURPLE + "Assistants: " + tt(pr.assistants) + "\n"
                                            + ChatColor.GOLD + "+--------------------+" + "\n"
                                            + ChatColor.GREEN + "Current percentage: " + pr.percentage.toString() + "%" + "\n"
                                            + ChatColor.GREEN + "Extimated Time: " + time(r) + "\n"
                                            + ChatColor.GOLD + "Other statistics:" + "\n"
                                            + ChatColor.GREEN + "Hours of work: " + Math.round(pr.minutes / 60) + "\n"
                                            + ChatColor.GREEN + "People that works on: " + people(r2) + "\n"
                                            + ChatColor.GOLD + "+--------------------+");

                                } else {
                                    if (pr.main) {
                                        message.addSimple(ChatColor.DARK_RED + "Main Project of the Server");
                                    }
                                    message.addSimple(ChatColor.BOLD.GOLD + "PROJECT: " + pr.name + "\n"
                                            + ChatColor.RED.BOLD + "Project Leader: " + ps + "\n"
                                            + ChatColor.GOLD + pr.description + "\n"
                                            + ChatColor.DARK_PURPLE + "Assistants: " + tt(pr.assistants) + "\n"
                                            + ChatColor.GOLD + "+--------------------+" + "\n"
                                            + ChatColor.GREEN + "Current percentage: " + pr.percentage.toString() + "%" + "\n"
                                            + ChatColor.GREEN + "Extimated Time: " + time(r) + "\n"
                                            + ChatColor.GOLD + "Other statistics:" + "\n"
                                            + ChatColor.GREEN + "Hours of work: " + Math.round(pr.minutes / 60) + "\n"
                                            + ChatColor.GREEN + "People that works on: " + people(r2) + "\n"
                                            + ChatColor.GOLD + "+--------------------+"
                                    );
                                }

                                if (!pr.status.equals(ProjectStatus.FINISHED)) {

                                    jj(pr);
                                    if (!jobs.isEmpty()) {
                                        message.addSimple(ChatColor.AQUA + "\n" + "Jobs linked to this project: " + "\n" + job());

                                        message.addSimple("\n" + ChatColor.GOLD + "+--------------------+");
                                    } else {
                                        message.addSimple(ChatColor.AQUA + "\n" + "No jobs linked to this project");
                                        message.addSimple("\n" + ChatColor.GOLD + "+--------------------+");
                                    }

                                    if (PluginData.regionsReadable.containsKey(PluginData.projectsAll.get(args[0]).idproject)) {

                                        for (String region : PluginData.regionsReadable.get(PluginData.projectsAll.get(args[0]).idproject)) {

                                            message.addSimple("\n" + ChatColor.AQUA + region.toUpperCase() + ": ");
                                            if (PluginData.warps.containsKey(PluginData.regions.get(region).idr)) {
                                                message.addClickable(ChatColor.GREEN.UNDERLINE + "Click to teleport", "/project warp " + pr.name + " " + region).setRunDirect();

                                            } else {
                                                message.addSimple(ChatColor.RED + "No warp available for this region");

                                            }

                                            messages.add(message);

                                        }
                                    }
                                }

                                if (!pr.link.equalsIgnoreCase("Nothing")) {
                                    message.addFancy("\n" + ChatColor.LIGHT_PURPLE + "Forum Thread", pr.link, "Click to go on the forum");
                                }

                                messages.add(message);

                                PluginData.getMessageUtil().sendFancyListMessage((Player) cs, header, messages, "/project details " + pr.name, 1);

                            } catch (SQLException ex) {
                                Logger.getLogger(ProjectDetails.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                    //
                }

            } else {

                sendNoProject(cs);

            }

        }

    }

    public static String people(ResultSet r) throws NullPointerException, SQLException {

        pers.clear();

        if (r.first()) {

            do {

                if (r.getLong("blocks") > 150
                        && ((System.currentTimeMillis() - r.getLong("lastplayed")) * 1000) < 604800) {
//1 week
                    OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(r.getString("player_uuid")));

                    pers.add(p.getName());
                }

            } while (r.next());

        }

        return String.valueOf(pers.size());

    }

    public static String tt(List<UUID> r) throws NullPointerException, SQLException {

        StringBuilder builder = new StringBuilder();

        for (UUID value : r) {
            int index = r.size() - 1;
            OfflinePlayer off = Bukkit.getOfflinePlayer(value);
            UUID val = r.get(index);
            if (!value.equals(val)) {

                builder.append(off.getName() + ",");
            } else {
                builder.append(off.getName() + " ");
            }

        }
        String text = builder.toString();

        return text;

    }

    public static String job() throws NullPointerException {

        StringBuilder builder = new StringBuilder();

        for (String value : jobs) {
            int index = jobs.size() - 1;

            String val = jobs.get(index);
            if (!value.equals(val)) {

                builder.append(value + ",");
            } else {
                builder.append(value + " ");
            }

        }
        String text = builder.toString();

        return text;

    }

    public static void jj(ProjectData pr) {
        jobs.clear();

        for (String value : pr.jobs) {
            if (JobDatabase.getActiveJobs().containsKey(value)) {

                if (JobDatabase.getActiveJobs().get(value).isPaused()) {
                    jobs.add(value);
                } else if (JobDatabase.getActiveJobs().get(value).isPrivate()) {

                } else {
                    jobs.add(value);
                }
            }

        }
    }

    public static String time(Long seconds) {

        Long days = seconds / 86400000;

        System.out.println("giorni" + days + " " + seconds);
        if (days < 7 && days > 0) {

            return days + " days";

        } else if (days >= 7 && days <= 28) {

            Long weeks = days / 7;

            return weeks + " weeks";

        } else if (days > 28 && days < 31) {

            Long y = days - 28;
            return "4 weeks and " + y + " days";

        } else if (days >= 31 && days <= 341) {

            Long months = days / 31;

            Long rd = days - (months * 31);
            if (rd != 0) {

                return months + " months and " + rd + " days";
            } else {

                return months + " months";
            }

        } else if (days > 341 && days < 365) {
            Long y = days - 341;

            return "11 months and " + y + " days";
        } else if (days >= 365) {
            Long years = days / 365;
            Long ys = days - (years * 365);

            return years + " years and " + ys + " days";

        } else {

            return "N/A";

        }

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendProjectError(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is hidden or it has been marked as finished!");
    }

}
