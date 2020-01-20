
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
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

                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                try {
                                    String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".news_data WHERE player_uuid = " + pl.getUniqueId().toString() + " AND idproject = " + PluginData.projectsAll.get(args[0]).idproject.toString() + " ;";

                                    final ResultSet r = Mcproject.getPluginInstance().con.prepareStatement(statement).executeQuery();

                                    if (!r.first()) {
                                        String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".news_data (idproject, player_uuid) VALUES ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','" + pl.getUniqueId().toString() + "') ;";
                                    }
                                } catch (SQLException ex) {
                                    Logger.getLogger(ProjectDetails.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 60L);

                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

                if (!pl.hasPermission("project.manager")) {
                    if (PluginData.projectsAll.get(args[0]).status.equals(ProjectStatus.SHOWED)) {

                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                try {
                                    ProjectData pr = PluginData.projectsAll.get(args[0]);

                                    String stat2 = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".people_data WHERE idproject = " + pr.idproject.toString() + " ;";
                                    String stat3 = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".staff_data WHERE idproject = " + pr.idproject.toString() + " ;";

                                    final ResultSet r2 = Mcproject.getPluginInstance().con.prepareStatement(stat2).executeQuery();
                                    final ResultSet r3 = Mcproject.getPluginInstance().con.prepareStatement(stat3).executeQuery();

                                    Long r = (pr.time - System.currentTimeMillis()) / 1000;

                                    //seconds
                                    FancyMessage header = new FancyMessage(MessageType.INFO, PluginData.getMessageUtil())
                                            .addSimple("Informations about " + pr.name);
                                    List<FancyMessage> messages = new ArrayList<>();

                                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                    String ps = Bukkit.getPlayer(pr.head).getName();
                                    message.addSimple(ChatColor.BOLD.GOLD + "PROJECT: " + pr.name.toUpperCase() + "\n"
                                            + ChatColor.RED.BOLD + "Head Project: " + ps + "\n"
                                            + ChatColor.GOLD + pr.description + "\n"
                                            + ChatColor.DARK_PURPLE + "Assistants: " + tt(r3) + "\n"
                                            + ChatColor.GOLD + "~--------------------~" + "\n"
                                            + ChatColor.GREEN + "Current percentage: " + pr.percentage.toString() + "%" + "\n"
                                            + ChatColor.GREEN + "Extimated Time: " + time(r) + "\n"
                                            + ChatColor.GOLD + "Other statistics:" + "\n"
                                            + ChatColor.GREEN + "Hours of work: " + Math.round(sumMinutes(r2) / 60) + "\n"
                                            + ChatColor.GREEN + "People that works on: " + people(r2) + "\n"
                                            + ChatColor.GOLD + "~--------------------~"
                                    );
                                    jj(pr);

                                    if (!jobs.isEmpty()) {
                                        message.addSimple(ChatColor.AQUA + "\n" + "Jobs linked to this project: " + "\n" + job());

                                        message.addSimple("\n" + ChatColor.GOLD + "~--------------------~");
                                    } else {
                                        message.addSimple(ChatColor.RED + "\n" + "No jobs linked to this project");
                                        message.addSimple("\n" + ChatColor.GOLD + "~--------------------~");
                                    }

                                    for (String region : PluginData.regionsReadable.get(PluginData.projectsAll.get(args[0]).idproject)) {

                                        message.addSimple("\n" + ChatColor.AQUA + region.toUpperCase() + ": ");
                                        if (PluginData.warps.containsKey(PluginData.regions.get(region).idr)) {
                                            message.addClickable(ChatColor.GREEN.UNDERLINE + "Click to teleport", "/project tp " + pr.name + " " + region).setRunDirect();

                                        } else {
                                            message.addSimple(ChatColor.RED + "No warp avaible for this region");

                                        }

                                        messages.add(message);

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

                    } else {

                        sendProjectError(cs);

                    }
                } else {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            try {
                                ProjectData pr = PluginData.projectsAll.get(args[0]);

                                String stat2 = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".people_data WHERE idproject = " + pr.idproject.toString() + " ;";
                                String stat3 = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".staff_data WHERE idproject = " + pr.idproject.toString() + " ;";

                                final ResultSet r2 = Mcproject.getPluginInstance().con.prepareStatement(stat2).executeQuery();
                                final ResultSet r3 = Mcproject.getPluginInstance().con.prepareStatement(stat3).executeQuery();

                                Long r = (pr.time - System.currentTimeMillis()) / 1000;

                                //seconds
                                FancyMessage header = new FancyMessage(MessageType.INFO, PluginData.getMessageUtil())
                                        .addSimple("Informations about " + pr.name);
                                List<FancyMessage> messages = new ArrayList<>();

                                FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                String ps = Bukkit.getPlayer(pr.head).getName();
                                if (pr.status.equals(ProjectStatus.FINISHED)) {

                                    message.addSimple(ChatColor.BOLD.GOLD + "PROJECT: " + pr.name.toUpperCase() + " (Finished)" + "\n"
                                            + ChatColor.RED.BOLD + "Head Project: " + ps + "\n"
                                            + ChatColor.GOLD + pr.description + "\n"
                                            + ChatColor.DARK_PURPLE + "Assistants: " + tt(r3) + "\n"
                                            + ChatColor.GOLD + "~--------------------~" + "\n"
                                            + ChatColor.GREEN + "Current percentage: " + pr.percentage.toString() + "%" + "\n"
                                            + ChatColor.GOLD + "Other statistics:" + "\n"
                                            + ChatColor.GREEN + "Hours of work: " + Math.round(sumMinutes(r2) / 60) + "\n"
                                            + ChatColor.GOLD + "~--------------------~"
                                    );

                                } else if (pr.status.equals(ProjectStatus.HIDDEN)) {

                                    message.addSimple(ChatColor.BOLD.GOLD + "PROJECT: " + pr.name.toUpperCase() + " (Hidden)" + "\n"
                                            + ChatColor.RED.BOLD + "Head Project: " + ps + "\n"
                                            + ChatColor.GOLD + pr.description + "\n"
                                            + ChatColor.DARK_PURPLE + "Assistants: " + tt(r3) + "\n"
                                            + ChatColor.GOLD + "~--------------------~" + "\n"
                                            + ChatColor.GREEN + "Current percentage: " + pr.percentage.toString() + "%" + "\n"
                                            + ChatColor.GREEN + "Extimated Time: " + time(r) + "\n"
                                            + ChatColor.GOLD + "Other statistics:" + "\n"
                                            + ChatColor.GREEN + "Hours of work: " + Math.round(sumMinutes(r2) / 60) + "\n"
                                            + ChatColor.GREEN + "People that works on: " + people(r2) + "\n"
                                            + ChatColor.GOLD + "~--------------------~");

                                } else {
                                    message.addSimple(ChatColor.BOLD.GOLD + "PROJECT: " + pr.name.toUpperCase() + "\n"
                                            + ChatColor.RED.BOLD + "Head Project: " + ps + "\n"
                                            + ChatColor.GOLD + pr.description + "\n"
                                            + ChatColor.DARK_PURPLE + "Assistants: " + tt(r3) + "\n"
                                            + ChatColor.GOLD + "~--------------------~" + "\n"
                                            + ChatColor.GREEN + "Current percentage: " + pr.percentage.toString() + "%" + "\n"
                                            + ChatColor.GREEN + "Extimated Time: " + time(r) + "\n"
                                            + ChatColor.GOLD + "Other statistics:" + "\n"
                                            + ChatColor.GREEN + "Hours of work: " + Math.round(sumMinutes(r2) / 60) + "\n"
                                            + ChatColor.GREEN + "People that works on: " + people(r2) + "\n"
                                            + ChatColor.GOLD + "~--------------------~"
                                    );
                                }

                                if (!pr.status.equals(ProjectStatus.FINISHED)) {

                                    jj(pr);
                                    if (jobs.size() != 0) {
                                        message.addSimple(ChatColor.AQUA + "\n" + "Jobs linked to this project: " + "\n" + job());

                                        message.addSimple("\n" + ChatColor.GOLD + "~--------------------~");
                                    } else {
                                        message.addSimple(ChatColor.AQUA + "\n" + "No jobs linked to this project");
                                        message.addSimple("\n" + ChatColor.GOLD + "~--------------------~");
                                    }
                                }
                                if (!pr.status.equals(ProjectStatus.FINISHED)) {
                                    for (String region : PluginData.regionsReadable.get(PluginData.projectsAll.get(args[0]).idproject)) {

                                        message.addSimple("\n" + ChatColor.AQUA + region.toUpperCase() + ": ");
                                        if (PluginData.warps.containsKey(PluginData.regions.get(region).idr)) {
                                            message.addClickable(ChatColor.GREEN.UNDERLINE + "Click to teleport", "/project tp " + pr.name + " " + region).setRunDirect();

                                        } else {
                                            message.addSimple(ChatColor.RED + "No warp avaible for this region");

                                        }

                                        messages.add(message);

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

    public static String tt(ResultSet r) throws NullPointerException, SQLException {

        List<String> list = new ArrayList();
        if (r.first()) {
            do {
                OfflinePlayer off = Bukkit.getOfflinePlayer(UUID.fromString(r.getString("staff_uuid")));
                list.add(off.getName());

            } while (r.next());

        }

        StringBuilder builder = new StringBuilder();

        for (String value : list) {
            int index = list.size() - 1;

            String val = list.get(index);
            if (!value.equals(val)) {

                builder.append(value + ",");
            } else {
                builder.append(value + " ");
            }

        }
        String text = builder.toString();

        return text;

    }

    public static Integer sumMinutes(ResultSet r) throws SQLException {
        Integer t = 0;

        if (r.first()) {

            do {

                t += r.getInt("minutes");

            } while (r.next());

        }

        return t;

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

        Long days = ((seconds / 60) / 60) / 24;

        if (days < 7 && days > 0) {

            return String.valueOf(Math.round(days)) + " days";

        } else if (days >= 7 && days <= 28) {

            Long weeks = days / 7;

            return String.valueOf(Math.round(weeks)) + " weeks";

        } else if (days > 28 && days < 31) {

            Long y = days - 28;
            return "4 weeks and " + String.valueOf(Math.round(y)) + " days";

        } else if (days >= 31 && days <= 341) {

            Long months = days / 31;

            Long rd = days - (Math.round(months) * 31);
            if (rd != 0) {

                return months + " months and " + Math.round(rd) + " days";
            } else {

                return months + " months";
            }

        } else if (days > 341 && days < 365) {
            Long y = days - 341;

            return "11 months and " + String.valueOf(Math.round(y)) + " days";
        } else if (days >= 365) {
            Long years = days / 365;
            Long ys = days - (Math.round(years) * 365);

            return Math.round(years) + " years and " + Math.round(ys) + " days";

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
