/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.runnables;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class SystemRunnable {

    public static void startDatabaseRecoveryRunnable() {

        new BukkitRunnable() {

            @Override
            public void run() {
                PluginData.loadProjects();
                new BukkitRunnable() {

                    @Override
                    public void run() {

                        PluginData.loadRegions();
                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                PluginData.loadWarps();
                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 5L);
                    }

                }.runTaskLater(Mcproject.getPluginInstance(), 100L);

            }

        }.runTaskTimer(Mcproject.getPluginInstance(), 100L, 36000L);

    }

}
