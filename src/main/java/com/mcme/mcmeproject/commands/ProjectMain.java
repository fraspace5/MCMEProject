/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author fraspace5
 */
public class ProjectMain extends ProjectCommand {

    public ProjectMain(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Set a project as the main project of the server");
        setUsageDescription(" <ProjectName> : Set this project as main");
    }

    public static List<String> mainproject;

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {

            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {

                    ProjectData pr = PluginData.getProjectdata().get(args[0]);
                    createList();
                    if (pr.main == true) {
                        sendAlreadyMain(cs);
                    } else {

                        for (String s : mainproject) {
                            ProjectData p = PluginData.getProjectdata().get(s);

                            p.main = false;

                        }

                        pr.main = true;
                        sendDone(cs, args[0]);

                    }
                }
            } else {

                sendNoProject(cs);

            }

        }

    }

    public static void createList() {
        mainproject.clear();
        for (String name : PluginData.getProjectdata().keySet()) {

            if (PluginData.getProjectdata().get(name).main == true) {
                mainproject.add(name);
            }

        }

    }

   public boolean playerPermission(String prr, CommandSender cs) {
        ProjectData pr = PluginData.getProjectdata().get(prr);
        Player pl = (Player) cs;
        if (pl.hasPermission("project.owner")) {
            return true;
        } else {
            sendNoPermission(cs);
            return false;
        }
    }

    private void sendNoPermission(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't manage this project");
    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendAlreadyMain(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This project is already the main project of the server.");
    }

    private void sendDone(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, name + " is the new main project of MCME");
    }
}
