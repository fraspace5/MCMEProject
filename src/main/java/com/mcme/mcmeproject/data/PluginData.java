/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.data;

import com.mcme.mcmeproject.Mcproject;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

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
    private final static Map<String, ProjectData> projectdata = new HashMap<>();

    @Getter
    private static Long time = Mcproject.getPluginInstance().getConfig().getLong("time");
    @Getter
    private static Boolean playernotification = Mcproject.getPluginInstance().getConfig().getBoolean("playernotification");
    @Getter
    private static Boolean main = Mcproject.getPluginInstance().getConfig().getBoolean("mainworld");
    @Getter
    private static Map<UUID, Boolean> min = new HashMap<>();

    @Getter
    private static Map<UUID, Boolean> news = new HashMap<>();

    @Setter
    @Getter
    private static Long t;

    @Getter
    private static List<String> today = new ArrayList();

    public static void onSave(File projectFolder) throws IOException {
        Mcproject.getPluginInstance().getClogger().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "MCMEProject" + ChatColor.DARK_GRAY + "] - " + ChatColor.BLUE + "Saving " + ChatColor.DARK_GRAY + projectdata.size() + " projects...");
        for (String projectName : projectdata.keySet()) {

            //create a file object
            File projectFile = new File(projectFolder, projectName + ".yml");

            //save the project data to that section
            projectdata.get(projectName).save(projectFile);

        }

    }

    public static void onLoad(File projectFolder) throws IOException, FileNotFoundException, InvalidConfigurationException {

        //clear old data in case of a reload
        projectdata.clear();
        Mcproject.getPluginInstance().getClogger().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "MCMEProject" + ChatColor.DARK_GRAY + "] - " + ChatColor.BLUE + "Loading projects: " + ChatColor.DARK_GRAY + projectFolder.listFiles().length + ChatColor.BLUE + " Found");
        for (File projectFile : projectFolder.listFiles()) {

            //create a new ProjectData object from the data in the file and put it in the projectData map
            projectdata.put(projectFile.getName().substring(0, projectFile.getName().length() - 4), new ProjectData(projectFile));
        }

    }

    public static void saveTime(File file, Long l) throws FileNotFoundException {
        String s = l.toString();

        try (PrintWriter out = new PrintWriter(new FileOutputStream(file))) {
            out.println(s);
        }

    }

    public static void loadTime(File file) throws FileNotFoundException {

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                Long l = parseLong(line);
                setT(l);
            }
        }

    }

    public static String serialize(UUID uuid, Boolean bool) {
        return uuid + ";" + bool;
    }

    public static void unserialize(String line) {

        String[] dataArray = line.split(";");
        UUID uuid = UUID.fromString(dataArray[0]);
        Boolean bool = Boolean.getBoolean(dataArray[1]);
        news.put(uuid, bool);
    }

    public static void saveBoolean(File file) throws FileNotFoundException {
        for (Entry<UUID, Boolean> data : news.entrySet()) {

            String storageData = PluginData.serialize(data.getKey(), data.getValue());

            try (PrintWriter out = new PrintWriter(new FileOutputStream(file))) {
                out.println(storageData);
            }
        }
    }

    public static void loadBoolean(File file) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                unserialize(line);

            }
        }
    }

    public static void setTodayEnd() {
        today.clear();
        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        for (String project : projectdata.keySet()) {

            Integer milliweeks = 1000 * 60 * 60 * 24 * 7;

            Long l = projectdata.get(project).updated + (milliweeks * time);

            cal.setTimeInMillis(l);
            int d = cal.get(Calendar.DAY_OF_MONTH);
            int m = cal.get(Calendar.MONTH);
            int dd = now.get(Calendar.DAY_OF_MONTH);
            int mm = now.get(Calendar.MONTH);

            if (d == dd && m == mm) {

                today.add(project);

            }

        }

    }

    public static void sendNews(Player pl) {

        List<String> projects = new ArrayList<>();

        for (String name : projectdata.keySet()) {

            if (!projectdata.get(name).news.contains(name)) {

                projects.add(name);

            }
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

}
