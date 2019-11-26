/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.data.PluginData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author fraspace5
 */
public class ProjectNews extends ProjectCommand {

    public ProjectNews(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Get or not news about project update");
        setUsageDescription(" true|false ");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {
            Player pl = (Player) cs;

            if (args[0].equals("true") || args[0].equals("false")) {

                switch (args[0]) {
                    case "true":

                        if (PluginData.getNews().get(pl.getUniqueId()) == false) {
                            PluginData.getNews().remove(pl.getUniqueId());
                            PluginData.getNews().put(pl.getUniqueId(), Boolean.TRUE);
                            sendDone(cs);
                        } else {
                            sendTrue(cs);
                        }

                        break;

                    case "false":
                        if (PluginData.getNews().get(pl.getUniqueId()) == true) {
                            PluginData.getNews().remove(pl.getUniqueId());
                            PluginData.getNews().put(pl.getUniqueId(), Boolean.FALSE);
                            sendDone(cs);
                        } else {
                            sendFalse(cs);
                        }
                        break;
                    default:

                        PluginData.getNews().remove(pl.getUniqueId());
                        PluginData.getNews().put(pl.getUniqueId(), Boolean.TRUE);

                        break;

                }
            } else {
                sendError(cs);
            }

        }

    }

    private void sendTrue(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "It is already set as true!");
    }

    private void sendError(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Should be true or false!");
    }

    private void sendFalse(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "It is already set as false!");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Done!");
    }
}
