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
import com.mcme.mcmeproject.data.ProjectData;
import com.mcme.mcmeproject.util.ProjectStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 *
 * @author Fraspace5
 */
public class ProjectCommandExecutor implements CommandExecutor, TabExecutor {

    @Getter
    private final Map<String, ProjectCommand> commands = new LinkedHashMap<>();

    private final String permission = "project.user";
    private final String permissionStaff = "project.manager";
    private final String permissionCreate = "project.create";
    private final String permissionReload = "project.reload";
    private final String permissionOwner = "project.owner";

    public ProjectCommandExecutor() {
        addCommandHandler("add", new ProjectAdd(permissionStaff, permissionOwner));
        addCommandHandler("area", new ProjectArea(permissionStaff, permissionOwner));
        addCommandHandler("create", new ProjectCreate(permissionCreate, permissionOwner));
        addCommandHandler("description", new ProjectDescription(permissionStaff, permissionOwner));
        addCommandHandler("leader", new ProjectLeader(permissionStaff, permissionOwner));
        addCommandHandler("link", new ProjectLink(permissionStaff, permissionOwner));
        addCommandHandler("list", new ProjectList(permission, permissionStaff, permissionOwner));
        addCommandHandler("name", new ProjectName(permissionStaff, permissionOwner));
        addCommandHandler("percentage", new ProjectPercentage(permissionStaff, permissionOwner));
        addCommandHandler("progress", new ProjectProgress(permissionStaff, permissionOwner));
        addCommandHandler("remove", new ProjectRemove(permissionStaff, permissionOwner));
        addCommandHandler("time", new ProjectTime(permissionStaff, permissionOwner));
        addCommandHandler("location", new ProjectLocation(permissionStaff, permissionOwner));
        addCommandHandler("details", new ProjectDetails(permission, permissionStaff, permissionOwner));
        addCommandHandler("warp", new ProjectWarp(permission, permissionStaff, permissionOwner));
        addCommandHandler("show", new ProjectShow(permissionStaff, permissionOwner));
        addCommandHandler("hide", new ProjectHide(permissionStaff, permissionOwner));
        addCommandHandler("finish", new ProjectFinish(permissionStaff, permissionOwner));
        addCommandHandler("help", new ProjectHelp(permission, permissionStaff, permissionOwner));
        addCommandHandler("reopen", new ProjectReopen(permissionStaff, permissionOwner));
        addCommandHandler("main", new ProjectMain(permissionStaff, permissionOwner));
        addCommandHandler("news", new ProjectNews(permission, permissionStaff, permissionOwner));
        addCommandHandler("reload", new ProjectReload(permissionReload));
        addCommandHandler("statistic", new ProjectStatistics(permission, permissionStaff, permissionOwner));
       // addCommandHandler("current", new CurrentProject(permission, permissionStaff, permissionOwner));
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (!string.equalsIgnoreCase("project")) {
            return false;
        }
        if (strings == null || strings.length == 0) {
            sendNoSubcommandErrorMessage(cs);
            return true;
        }
        if (commands.containsKey(strings[0].toLowerCase())) {
            commands.get(strings[0].toLowerCase()).handle(cs, Arrays.copyOfRange(strings, 1, strings.length));
        } else {
            sendSubcommandNotFoundErrorMessage(cs);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("project")) {
            Player pl = (Player) sender;
            List<String> arguments = new ArrayList<>();
            arguments.add("list");
            arguments.add("warp");
            arguments.add("details");
            arguments.add("help");
            arguments.add("news");
            arguments.add("statistic");
           // arguments.add("current");
            if (pl.hasPermission("project.manager") || pl.hasPermission("project.owner")) {
                arguments.add("show");
                arguments.add("hide");
                arguments.add("add");
                arguments.add("create");
                arguments.add("area");
                arguments.add("description");
                arguments.add("leader");
                arguments.add("link");
                arguments.add("name");
                arguments.add("percentage");
                arguments.add("progress");
                arguments.add("remove");
                arguments.add("time");
                arguments.add("location");
                arguments.add("finish");
                arguments.add("reopen");
                arguments.add("main");
                arguments.add("reload");
            }

            //                                       2      1        1       2      3        1             2       2        2        2              3          2         2      2        1        2       1       0        1
            //                                       /      /        /       /      /       /              /       /        /        /              /          /       /      /       /         !       /       /        /
            List<String> Flist = new ArrayList<String>();

            if (args.length == 1) {
                for (String s : arguments) {
                    if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                        Flist.add(s);
                    }
                }
                return Flist;
            } else if (args.length == 2) {

                List<String> ProjectList = new ArrayList<>();
                List<String> ProjectListShowed = new ArrayList<>();
                List<String> ProjectListHidden = new ArrayList<>();
                List<String> ProjectListFinished = new ArrayList<>();

                for (Entry<String, ProjectData> entry : PluginData.projectsAll.entrySet()) {
                    ProjectStatus lowerCaseKey = entry.getValue().status;

                    switch (lowerCaseKey) {
                        case HIDDEN:
                            ProjectListHidden.add(entry.getValue().name);
                            ProjectList.add(entry.getValue().name);
                            break;
                        case SHOWED:
                            ProjectListShowed.add(entry.getValue().name);
                            ProjectList.add(entry.getValue().name);
                            break;
                        case FINISHED:
                            ProjectListFinished.add(entry.getValue().name);
                            break;
                        default:
                            break;
                    }

                }

                List<String> fproject = new ArrayList<>();
                List<String> fshowed = new ArrayList<>();
                List<String> fhidden = new ArrayList<>();
                List<String> ffinished = new ArrayList<>();
                List<String> fotherlist = new ArrayList<>();
                if (args[0].equalsIgnoreCase("show")) {

                    for (String s : ProjectListHidden) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fhidden.add(s);
                        }
                    }
                    return fhidden;

                } else if (args[0].equalsIgnoreCase("hide")) {
                    for (String s : ProjectListShowed) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fshowed.add(s);
                        }
                    }
                    return fshowed;

                } else if (args[0].equalsIgnoreCase("description")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;

                } else if (args[0].equalsIgnoreCase("main")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;

                } else if (args[0].equalsIgnoreCase("details")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;

                }else if (args[0].equalsIgnoreCase("current")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;

                } else if (args[0].equalsIgnoreCase("reopen")) {
                    for (String s : ProjectListFinished) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            ffinished.add(s);
                        }
                    }
                    return ffinished;

                } else if (args[0].equalsIgnoreCase("finish")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("list")) {

                    List<String> a = Arrays.asList("1", "2", "3");
                    for (String s : a) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fotherlist.add(s);
                        }
                    }
                    return fotherlist;

                } else if (args[0].equalsIgnoreCase("reload")) {

                    List<String> a = Arrays.asList("map", "projects", "all","regions","warps");
                    for (String s : a) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fotherlist.add(s);
                        }
                    }
                    return fotherlist;

                } else if (args[0].equalsIgnoreCase("statistic")) {

                    List<String> a = Arrays.asList("week", "today", "month", "custom");
                    for (String s : a) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fotherlist.add(s);
                        }
                    }
                    return fotherlist;

                } else if (args[0].equalsIgnoreCase("news")) {

                    List<String> a = Arrays.asList("true", "false");
                    for (String s : a) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fotherlist.add(s);
                        }
                    }
                    return fotherlist;

                } else if (args[0].equalsIgnoreCase("add")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("area")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("leader")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("link")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("name")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("percentage")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("progress")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("location")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("time")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("warp")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else if (args[0].equalsIgnoreCase("remove")) {
                    for (String s : ProjectList) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            fproject.add(s);
                        }
                    }
                    return fproject;
                } else {

                    return null;
                }

            } else if (args.length == 3) {

                List<String> RegionList = new ArrayList<>();
                List<String> fregion = new ArrayList<>();
                List<String> fotherlist = new ArrayList<>();
                List<String> fo2 = new ArrayList<>();
                if (PluginData.projectsAll.containsKey(args[1])) {
                    if (PluginData.regionsReadable.containsKey(PluginData.projectsAll.get(args[1]).idproject) && !PluginData.regionsReadable.get(PluginData.projectsAll.get(args[1]).idproject).isEmpty()) {
                        RegionList = PluginData.regionsReadable.get(PluginData.projectsAll.get(args[1]).idproject);

                    }
                }

                if (args[0].equalsIgnoreCase("add")) {

                    return null;
                } else if (args[0].equalsIgnoreCase("remove")) {

                    return null;
                } else if (args[0].equalsIgnoreCase("leader")) {

                    return null;
                } else if (args[0].equalsIgnoreCase("link")) {

                    return null;
                } else if (args[0].equalsIgnoreCase("name")) {

                    return null;
                } else if (args[0].equalsIgnoreCase("percentage")) {

                    return null;
                } else if (args[0].equalsIgnoreCase("list")) {

                    if (pl.hasPermission("project.manager")) {
                        List<String> l = Arrays.asList("archive");
                        return l;

                    } else {
                        return null;
                    }
                } else if (args[0].equalsIgnoreCase("statistic")) {

                    if (args[1].equalsIgnoreCase("custom")) {
                        List<String> l = Arrays.asList("dd/mm/yyyy");

                        return l;

                    } else {
                        return null;
                    }
                } else if (args[0].equalsIgnoreCase("time")) {

                    return null;
                } else if (args[0].equalsIgnoreCase("location")) {

                    for (String s : RegionList) {
                        if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                            fregion.add(s);
                        }
                    }
                    return fregion;
                } else if (args[0].equalsIgnoreCase("warp")) {

                    for (String s : RegionList) {
                        if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                            fregion.add(s);
                        }
                    }
                    return fregion;
                } else if (args[0].equalsIgnoreCase("progress")) {

                    List<String> l = Arrays.asList("percentage", "=");
                    for (String s : l) {
                        if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                            fo2.add(s);
                        }
                    }

                    return fo2;

                } else if (args[0].equalsIgnoreCase("area")) {

                    List<String> l = Arrays.asList("add", "remove");
                    for (String s : l) {
                        if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                            fotherlist.add(s);
                        }
                    }
                    return fotherlist;

                } else {
                    return null;
                }

            } else if (args.length == 4) {

                List<String> RegionList = new ArrayList<>();
                List<String> fregion = new ArrayList<>();
                List<String> fo2 = new ArrayList<>();

                if (PluginData.projectsAll.containsKey(args[1])) {
                    if (PluginData.regionsReadable.containsKey(PluginData.projectsAll.get(args[1]).idproject) && !PluginData.regionsReadable.get(PluginData.projectsAll.get(args[1]).idproject).isEmpty()) {
                        RegionList = PluginData.regionsReadable.get(PluginData.projectsAll.get(args[1]).idproject);

                    }
                }

                if (args[0].equalsIgnoreCase("progress")) {

                    List<String> l = Arrays.asList("time", "=");
                    for (String s : l) {
                        if (s.toLowerCase().startsWith(args[3].toLowerCase())) {
                            fo2.add(s);
                        }
                    }
                    return fo2;

                } else if (args[0].equalsIgnoreCase("statistic")) {

                    if (args[1].equalsIgnoreCase("custom")) {
                        List<String> l = Arrays.asList("dd/mm/yyyy");
                        return l;

                    } else {
                        return null;
                    }
                } else if (args[0].equalsIgnoreCase("area")) {

                    if (args[2].equalsIgnoreCase("remove")) {
                        for (String s : RegionList) {
                            if (s.toLowerCase().startsWith(args[3].toLowerCase())) {
                                fregion.add(s);
                            }
                        }
                        return fregion;

                    } else {
                        return null;
                    }

                } else {
                    return null;
                }
            } else {
                return null;
            }

        } else {

            return null;
        }
    }

    private void sendNoSubcommandErrorMessage(CommandSender cs) {
        //MessageUtil.sendErrorMessage(cs, "You're missing subcommand name for this command.");
        PluginDescriptionFile descr = Mcproject.getPluginInstance().getDescription();
        PluginData.getMessageUtil().sendErrorMessage(cs, descr.getName() + " - version " + descr.getVersion());
    }

    private void sendSubcommandNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Subcommand not found.");
    }

    private void addCommandHandler(String name, ProjectCommand handler) {
        commands.put(name, handler);
    }

}
