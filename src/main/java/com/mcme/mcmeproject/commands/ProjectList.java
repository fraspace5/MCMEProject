/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.data.ProjectGotData;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Fraspace5
 */
public class ProjectList extends ProjectCommand {

    public ProjectList(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Show the list of project");
        setUsageDescription(" [#page] <historic>: Show the list of all ongoing projects.Add historic to see everything(Manager)");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        int pageIndex = 0;
        Player pl = (Player) cs;
        if (args.length > 0 && (!NumericUtil.isInt(args[0]))) {

            pageIndex = 1;
        }
        int page = 1;

        if (args.length > pageIndex && NumericUtil.isInt(args[pageIndex])) {
            page = NumericUtil.getInt(args[pageIndex]);
        }

        FancyMessage header = new FancyMessage(MessageType.WHITE, PluginData.getMessageUtil())
                .addSimple(ChatColor.DARK_GREEN + "Project opens (click for details)" + "\n" + ChatColor.GOLD + "~~~~~~~~~~~~~~~~~");
        List<FancyMessage> messages = new ArrayList<>();
        for (String project : PluginData.projectsAll.keySet()) {

            ProjectGotData pr = PluginData.projectsAll.get(project);

            if (pl.hasPermission("project.manager") && args.length > 1 && args[1].equalsIgnoreCase("historic")) {
                if (pr.status.equals(ProjectStatus.SHOWED)) {

                    if (pr.main == true) {

                        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                        message.addSimple(ChatColor.GOLD + "- ");
                        message.addFancy(ChatColor.DARK_RED + "MAIN " + ChatColor.DARK_GREEN + project, "/project details " + project, ChatColor.DARK_GREEN + pr.description);

                        message.addSimple("\n" + ChatColor.GOLD + "~~~~~~~~~~~~~~~~~");
                        messages.add(message);

                    } else {
                        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                        message.addSimple(ChatColor.GOLD + "- ");
                        message.addFancy(ChatColor.DARK_GREEN + project, "/project details " + project, ChatColor.DARK_GREEN + pr.description);

                        message.addSimple("\n" + ChatColor.GOLD + "~~~~~~~~~~~~~~~~~");
                        messages.add(message);
                    }

                } else if (pr.status.equals(ProjectStatus.HIDDEN)) {
                    if (pr.main == true) {

                        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                        message.addSimple(ChatColor.GOLD + "- ");
                        message.addFancy(ChatColor.DARK_RED + "MAIN " + ChatColor.YELLOW + project, "/project details " + project, ChatColor.DARK_GREEN + pr.description);

                        message.addSimple("\n" + ChatColor.GOLD + "~~~~~~~~~~~~~~~~~");
                        messages.add(message);
                    } else {
                        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                        message.addSimple(ChatColor.GOLD + "- ");
                        message.addFancy(ChatColor.YELLOW + project, "/project details " + project, ChatColor.DARK_GREEN + pr.description);

                        message.addSimple("\n" + ChatColor.GOLD + "~~~~~~~~~~~~~~~~~");
                        messages.add(message);
                    }

                } else {
                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                    message.addSimple(ChatColor.GOLD + "- ");
                    message.addFancy(ChatColor.DARK_RED + project, "/project details " + project, ChatColor.DARK_GREEN + pr.description);

                    message.addSimple("\n" + ChatColor.GOLD + "~~~~~~~~~~~~~~~~~");
                    messages.add(message);
                }

            } else {
                if (pr.main == true) {

                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                    message.addSimple(ChatColor.GOLD + "- ");
                    message.addFancy(ChatColor.DARK_RED + "MAIN " + ChatColor.DARK_GREEN + project, "/project details " + project, ChatColor.DARK_GREEN + pr.description);

                    message.addSimple("\n" + ChatColor.GOLD + "~~~~~~~~~~~~~~~~~");
                    messages.add(message);

                } else {
                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                    message.addSimple(ChatColor.GOLD + "- ");
                    message.addFancy(ChatColor.DARK_GREEN + project, "/project details " + project, ChatColor.DARK_GREEN + pr.description);

                    message.addSimple("\n" + ChatColor.GOLD + "~~~~~~~~~~~~~~~~~");
                    messages.add(message);
                }

            }

        }
        PluginData.getMessageUtil().sendFancyListMessage((Player) cs, header, messages, "/project list ", page);
    }

}
