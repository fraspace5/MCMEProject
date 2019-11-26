/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Fraspace5
 */
public class ProjectPercentage extends ProjectCommand {

    public ProjectPercentage(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Sets the percentage of the project.");
        setUsageDescription(" <ProjectName> <Percentage>: Set the percentage value of a project.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {

            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    if (parseDouble(args[1]) > 100.0) {

                        sendNoPercentage(cs);

                    } else {
                        PluginData.getProjectdata().get(args[0]).percentage = parseDouble(args[1]);
                        sendDone(cs);
                    }
                }
            } else {

                sendNoProject(cs);

            }

        }

    }

    public boolean playerPermission(String prr, CommandSender cs) {
        ProjectData pr = PluginData.getProjectdata().get(prr);
        Player pl = (Player) cs;
        if (pr.head.equals(pl.getUniqueId()) || pr.managers.contains(pl.getName()) || pl.hasPermission("project.owner")) {
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
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendNoPercentage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Percentage value should be less than 100 ");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Percentage updated!");
    }

}
