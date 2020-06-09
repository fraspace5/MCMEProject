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

import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
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
        setUsageDescription(" [#page] <archive>: Show the list of all ongoing projects.Add historic to see everything(Manager)");
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
                .addSimple(ChatColor.DARK_GREEN + "Project opens (click for details)" + "\n");
        List<FancyMessage> messages = new ArrayList<>();
        PluginData.getProjectsAll().keySet().forEach((project) -> {
            ProjectData pr = PluginData.getProjectsAll().get(project);
            String s = ChatColor.WHITE + "+" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.BLUE + "-" + ChatColor.GOLD + "-" + ChatColor.WHITE + "+";

            if (pl.hasPermission("project.manager") || pl.hasPermission("project.owner")) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("archive") || args[0].equalsIgnoreCase("archive")) {
                        switch (pr.getStatus()) {
                            case SHOWED:
                                if (pr.isMain() == true) {
                                    
                                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                    message.addSimple(ChatColor.GOLD + "- ");
                                    message.addFancy(ChatColor.DARK_RED + "MAIN " + ChatColor.DARK_GREEN + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());
                                    message.addSimple("\n" + s);
                                    messages.add(message);
                                    
                                } else {
                                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                    message.addSimple(ChatColor.GOLD + "- ");
                                    message.addFancy(ChatColor.DARK_GREEN + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());
                                    
                                    message.addSimple("\n" + s);
                                    messages.add(message);
                                }   break;
                            case HIDDEN:
                                if (pr.isMain() == true) {
                                    
                                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                    message.addSimple(ChatColor.GOLD + "- ");
                                    message.addFancy(ChatColor.DARK_RED + "MAIN " + ChatColor.YELLOW + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());
                                    
                                    message.addSimple("\n" + s);
                                    messages.add(message);
                                } else {
                                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                    message.addSimple(ChatColor.GOLD + "- ");
                                    message.addFancy(ChatColor.YELLOW + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());
                                    
                                    message.addSimple("\n" + s);
                                    messages.add(message);
                                }   break;
                            default:
                                FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                                message.addSimple(ChatColor.GOLD + "- ");
                                message.addFancy(ChatColor.DARK_RED + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());
                                message.addSimple("\n" + s);
                                messages.add(message);
                                break;
                        }
                    }

                } else {

                    if (pr.getStatus().equals(ProjectStatus.SHOWED)) {

                        if (pr.isMain() == true) {

                            FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                            message.addSimple(ChatColor.GOLD + "- ");
                            message.addFancy(ChatColor.DARK_RED + "MAIN " + ChatColor.DARK_GREEN + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());
                            message.addSimple("\n" + s);
                            messages.add(message);

                        } else {
                            FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                            message.addSimple(ChatColor.GOLD + "- ");
                            message.addFancy(ChatColor.DARK_GREEN + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());

                            message.addSimple("\n" + s);
                            messages.add(message);
                        }

                    } else if (pr.getStatus().equals(ProjectStatus.HIDDEN)) {
                        if (pr.isMain() == true) {

                            FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                            message.addSimple(ChatColor.GOLD + "- ");
                            message.addFancy(ChatColor.DARK_RED + "MAIN " + ChatColor.YELLOW + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());

                            message.addSimple("\n" + s);
                            messages.add(message);
                        } else {
                            FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                            message.addSimple(ChatColor.GOLD + "- ");
                            message.addFancy(ChatColor.YELLOW + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());

                            message.addSimple("\n" + s);
                            messages.add(message);
                        }

                    }

                }
            } else {
                if (pr.getStatus().equals(ProjectStatus.SHOWED)) {

                    if (pr.isMain() == true) {

                        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                        message.addSimple(ChatColor.GOLD + "- ");
                        message.addFancy(ChatColor.DARK_RED + "MAIN " + ChatColor.DARK_GREEN + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());

                        message.addSimple("\n" + s);
                        messages.add(message);

                    } else {
                        FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX, PluginData.getMessageUtil());
                        message.addSimple(ChatColor.GOLD + "- ");
                        message.addFancy(ChatColor.DARK_GREEN + project, "/project details " + project, ChatColor.DARK_GREEN + pr.getDescription());

                        message.addSimple("\n" + s);
                        messages.add(message);
                    }

                }

            }
        });
        PluginData.getMessageUtil().sendFancyListMessage((Player) cs, header, messages, "/project list ", page);
    }

}
