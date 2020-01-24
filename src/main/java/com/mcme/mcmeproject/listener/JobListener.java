/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.listener;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.thegaffer.events.JobEndEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Fraspace5
 */
import com.mcmiddleearth.thegaffer.events.JobStartEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.scheduler.BukkitRunnable;

public class JobListener implements Listener {

    @EventHandler
    public void onJobStart(JobStartEvent e) {

        String jobname = e.getJobName();
        String project = e.getJobProject();
        if (!project.equalsIgnoreCase("nothing")) {
            if (!PluginData.projectsAll.get(project).jobs.contains(jobname)) {

                final List<String> jobs = PluginData.projectsAll.get(project).jobs;

                jobs.add(jobname);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try {
                            String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET jobs = '" + serialize(jobs) + "' WHERE idproject = '" + PluginData.projectsAll.get(project).idproject.toString() + "' ;";
                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                            PluginData.loadProjects();
                        } catch (SQLException ex) {
                            Logger.getLogger(JobListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

            }

        }

    }

    @EventHandler
    public void onJobEnd(JobEndEvent e) {

        String jobname = e.getJobName();
        String project = e.getJobProject();
        if (!project.equalsIgnoreCase("nothing")) {
            if (PluginData.projectsAll.get(project).jobs.contains(jobname)) {

                final List<String> jobs = PluginData.projectsAll.get(project).jobs;

                jobs.remove(jobname);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try {
                            String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".project_data SET jobs = '" + serialize(jobs) + "' WHERE idproject = '" + PluginData.projectsAll.get(project).idproject.toString() + "' ;";
                            Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                            PluginData.loadProjects();
                        } catch (SQLException ex) {
                            Logger.getLogger(JobListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }.runTaskAsynchronously(Mcproject.getPluginInstance());

            }
        }

    }

    public String serialize(List<String> intlist) {

        StringBuilder builder = new StringBuilder();

        for (String s : intlist) {

            builder.append(s + ";");

        }

        return builder.toString();

    }

}
