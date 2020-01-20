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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
    protected void execute(final CommandSender cs, final String... args) {

        if (cs instanceof Player) {
            final Player pl = (Player) cs;
            if (!PluginData.getProjectsAll().containsKey(args[0])) {

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        try {
                            Date d = new Date(System.currentTimeMillis());

                            String stat = "INSERT INTO " + Mcproject.getPluginInstance().database + ".project_data (idproject, name, staff_uuid, startDate, percentage, link, time, description, update ) VALUES ('" + PluginData.getProjectsAll().get(args[0]).idproject.toString() + "','" + PluginData.getProjectsAll().get(args[0]).name + "','" + pl.getUniqueId().toString() + "','" + d.toString() + "','0','nothing','" + System.currentTimeMillis() + "',' '," + System.currentTimeMillis() + ") ;";
                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate();
                            //SEND TO OTHER SERVERS

                            PluginData.loadProjects();

                            sendCreated(cs, args[0]);
                        } catch (SQLException ex) {
                            Logger.getLogger(ProjectCreate.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

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
