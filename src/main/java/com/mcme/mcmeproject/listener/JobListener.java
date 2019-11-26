/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.listener;

import com.mcme.mcmeproject.data.PluginData;
import com.mcmiddleearth.thegaffer.events.JobEndEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Fraspace5
 */
import com.mcmiddleearth.thegaffer.events.JobStartEvent;

public class JobListener implements Listener {

    @EventHandler
    public void onJobStart(JobStartEvent e) {

        String jobname = e.getJobName();
        String project = e.getJobProject();
        if (!project.equalsIgnoreCase("nothing")) {
            PluginData.getProjectdata().get(project).jobs.add(jobname);
        }

    }

    @EventHandler
    public void onJobEnd(JobEndEvent e) {

        String jobname = e.getJobName();
        String project = e.getJobProject();
        if (!project.equalsIgnoreCase("nothing")) {
            PluginData.getProjectdata().get(project).jobs.remove(jobname);
        }

    }

}
