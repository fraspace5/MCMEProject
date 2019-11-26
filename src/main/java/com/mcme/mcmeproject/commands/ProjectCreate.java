/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Fraspace5
 */
public class ProjectCreate extends ProjectCommand {

    public ProjectCreate(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Create a new project");
        setUsageDescription(" <ProjectName>: Create a new project");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {
            Player pl = (Player) cs;
            if (!PluginData.getProjectdata().containsKey(args[0])) {
                ProjectData n = new ProjectData(args[0], pl);

                PluginData.getProjectdata().put(args[0], n);
                sendCreated(cs, args[0]);
                try {
                    PluginData.onSave(Mcproject.getPluginInstance().getProjectFolder());
                } catch (IOException ex) {
                    Logger.getLogger(ProjectCreate.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {

                sendAlreadyProject(cs);

            }

        }

    }

    

    private void sendAlreadyProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project already exists");
    }

    private void sendCreated(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "New project " + name + " created! Add new information, type /project help");
    }

}
